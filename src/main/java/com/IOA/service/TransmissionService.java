package com.IOA.service;

import com.IOA.dao.*;
import com.IOA.model.*;
import com.IOA.util.MyErrorType;
import com.IOA.util.MyPipe;
import com.IOA.util.SensorConfig;
import com.IOA.vo.NormalMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class TransmissionService {

    private final ResultDAO RDAO;

    private final DeviceDAO DDAO;

    private final SensorDAO SDAO;

    private final ThresholdDAO TDAO;

    private final SensorGreenhouseDAO SGDAO;

    private final MyPipe Pipe;

    private ListnerService listnerService;

    @Autowired
    public TransmissionService(ResultDAO RDAO,
                               DeviceDAO DDAO,
                               SensorDAO SDAO,
                               ThresholdDAO TDAO,
                               SensorGreenhouseDAO SGDAO,
                               MyPipe Pipe) throws IOException {
        this.RDAO = RDAO;
        this.DDAO = DDAO;
        this.SDAO = SDAO;
        this.TDAO = TDAO;
        this.SGDAO = SGDAO;
        this.Pipe = Pipe;
        this.listnerService = new ListnerService();
        new Thread(listnerService).start();
    }

    public NormalMessage sendOrder(Integer deviceId, String status) {
        List<DeviceModel> deviceArr = DDAO.searchBySomeId(deviceId, "id");
        if (deviceArr.size() == 0) {
            return new NormalMessage(false, MyErrorType.DeviceUnexist, null);
        }
        String clusterId = deviceArr.get(0).getClusterId();
        String nickname = deviceArr.get(0).getNickname();
        String order = SensorConfig.TranslateOrder(nickname, status);
        if (order == null) {
            return new NormalMessage(false, MyErrorType.OrderFail, null);
        } else {
            return listnerService.send(clusterId, order)
                    ? new NormalMessage(true, null, null)
                    : new NormalMessage(false, MyErrorType.OrderFail, null);
        }
    }


    class ListnerService implements Runnable {
        private ServerSocket serverSocket;
        private ExecutorService executorService;
        private Vector<Handler> handlerArr;

        ListnerService() throws IOException {
            int PORT = SensorConfig.PORT;
            this.serverSocket = new ServerSocket(PORT);
            int POOL_SIZE = 20;
            this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
            this.handlerArr = new Vector<>();
        }

        @Override
        public void run() {
            System.out.println("开始监听传感器");
            while (true) {
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Handler tmpHandler = new Handler(socket);
                this.handlerArr.add(tmpHandler);
                executorService.execute(tmpHandler);
                System.out.println("链接进入");
            }
        }

        boolean send(String clusterId, String order) {
            boolean res = false;
            for (Handler tmpHandler : this.handlerArr) {
                res = tmpHandler.send(clusterId, order);
                if (res)
                    break;
            }
            return res;
        }
    }

    class Handler implements Runnable {
        private Socket socket;
        private HashSet<String> idSet;

        Handler(Socket socket) {
            this.socket = socket;
            this.idSet = new HashSet<>();
        }

        private PrintWriter getWriter(Socket socket) throws IOException {
            OutputStream socketOut = socket.getOutputStream();
            return new PrintWriter(socketOut, true);
        }

        private BufferedReader getReader(Socket socket) throws IOException {
            InputStream socketIn = socket.getInputStream();
            return new BufferedReader(new InputStreamReader(socketIn));
        }

        @Override
        public void run() {
            BufferedReader in = null;
            try {
                in = this.getReader(this.socket);
                String msg = null;
                while ((msg = in.readLine()) != null) {

                    //////////// 收到传感器群信息后的逻辑 ////////////

                    // 拿个时间戳
                    Timestamp currentTime = new Timestamp(System.currentTimeMillis());

                    // 解析传感器群id
                    String clusterId = this.getId(msg);
                    this.idSet.add(clusterId);

                    // 拿到传感器群所属大棚，如果传感器不属于任何大棚，或传感器处于close或error状态则不记录数据
                    List<SensorGreenhouseModel> sensorGreenhouseModelList
                            = SGDAO.searchBySomeId(clusterId, "clusterId");
                    if (sensorGreenhouseModelList.size() != 0
                            && sensorGreenhouseModelList.get(0).getStatus().equals("on")) {

                        // 拿到这个传感器群中的所有传感器+设备
                        List<SensorModel> sensorArrOfCluster
                                = SDAO.searchBySomeId(clusterId, "clusterId");
                        List<DeviceModel> deviceArrOfCluster
                                = DDAO.searchBySomeId(clusterId, "clusterId");

                        // 把收到的信息转化成对应的数据结构
                        List<BigDecimal> rawSensorArr = this.parseSensor(msg);
                        Map<String, String> rawDeviceMap = this.parseDevice(msg);

                        // 根据位置或标号以及集群id，建立<传感器id-数据值>对
                        Map<Integer, Map<String, BigDecimal>> sensorMap
                                = this.convertSensorId(sensorArrOfCluster, rawSensorArr);
                        Map<Integer, String> deviceMap
                                = this.convertDeviceId(deviceArrOfCluster, rawDeviceMap);

                        // 塞入缓存及数据库
                        this.fulfillPipe(clusterId, sensorMap, deviceMap, currentTime);
                        this.fulfillDB(sensorGreenhouseModelList.get(0).getGreenhouseId(),
                                sensorMap, deviceMap, sensorArrOfCluster, currentTime);
                    }

                    //////////// 收到传感器群信息后的逻辑 ////////////
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socket != null)
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (in != null)
                        in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String getId(String msg) {
            return msg.substring(0, msg.indexOf(","));
        }

        private List<BigDecimal> parseSensor(String msg) {
            String usefulMsg = msg.substring(0, msg.indexOf(";"));
            List<String> tmp = Arrays.asList(usefulMsg.split(","));
            tmp = tmp.subList(2, tmp.size());
            return tmp.stream().map(BigDecimal::new).collect(Collectors.toList());
        }

        private Map<String, String> parseDevice(String msg) {
            String usefulMsg = msg.substring(msg.indexOf(";") + 1);
            String[] tmp = usefulMsg.split(";");
            Map<String, String> res = new HashMap<>();
            for (String ttmp : tmp) {
                String[] KV = ttmp.split(":");
                res.put(KV[0], KV[1].trim());
            }
            return res;
        }

        private Map<Integer, Map<String, BigDecimal>> convertSensorId(List<SensorModel> sensorArrOfCluster,
                                                                      List<BigDecimal> sensorArr) {
            Map<Integer, Map<String, BigDecimal>> res = new HashMap<>();
            for (SensorModel tmpSensor : sensorArrOfCluster) {
                int thresholdId = tmpSensor.getThresholdId();
                BigDecimal minimum = null;
                BigDecimal maximum = null;
                if (thresholdId != -1) {
                    List<ThresholdModel> thresholdArr =
                            TDAO.searchBySomeId(thresholdId, "id");
                    if (thresholdArr.size() != 0) {
                        minimum = thresholdArr.get(0).getMinimum();
                        maximum = thresholdArr.get(0).getMaximum();
                    }
                }

                int i = tmpSensor.getInnerId();
                BigDecimal value = sensorArr.get(i);

                Map<String, BigDecimal> tmpMap = new HashMap<>();
                tmpMap.put("value", value);
                tmpMap.put("minimum", minimum);
                tmpMap.put("maximum", maximum);
                res.put(tmpSensor.getId(), tmpMap);
            }
            return res;
        }

        private Map<Integer, String> convertDeviceId(List<DeviceModel> deviceArrOfCluster,
                                                     Map<String, String> deviceMap) {
            Map<Integer, String> res = new HashMap<>();
            for (DeviceModel tmpDevice : deviceArrOfCluster) {
                String key = tmpDevice.getNickname();
                res.put(tmpDevice.getId(), deviceMap.get(key));
            }
            return res;
        }

        private void fulfillPipe(String clusterId, Map<Integer, Map<String, BigDecimal>> sensorMap,
                                 Map<Integer, String> deviceMap, Timestamp currentTime) {
            Pipe.setSensor2Server(clusterId, sensorMap);
            Pipe.setDevice2Server(clusterId, deviceMap);
            Pipe.setRefreshTime(clusterId, currentTime);
        }

        private void fulfillDB(Integer greenhouseId,
                               Map<Integer, Map<String, BigDecimal>> sensorMap,
                               Map<Integer, String> deviceMap,
                               List<SensorModel> sensorArrOfCluster,
                               Timestamp currentTime) {
            // 存Result表
            for (SensorModel tmpSensor : sensorArrOfCluster) {
                int sensorId = tmpSensor.getId();
                BigDecimal value = sensorMap.get(sensorId) == null
                        ? null : sensorMap.get(sensorId).get("value");
                BigDecimal minimum = sensorMap.get(sensorId) == null
                        ? null : sensorMap.get(sensorId).get("minimum");
                BigDecimal maximum = sensorMap.get(sensorId) == null
                        ? null : sensorMap.get(sensorId).get("maximum");

                RDAO.save(new ResultModel(sensorId, value, currentTime,
                        minimum, maximum, greenhouseId));
            }

            // 更新Device表
            for (Map.Entry<Integer, String> entry : deviceMap.entrySet()) {
                Integer deviceId = entry.getKey();
                String status = entry.getValue();
                DDAO.updateStatus(deviceId, status);
            }
        }

        boolean send(String clusterId, String order) {
            boolean res = true;
            if (idSet.contains(clusterId)) {
                try {
                    PrintWriter out = this.getWriter(this.socket);
                    System.out.println("传达: " + order);
                    out.println(order);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    res = false;
                }
            } else {
                res = false;
            }
            return res;
        }
    }


}