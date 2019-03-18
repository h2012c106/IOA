package com.IOA.dao;

import com.IOA.model.ThresholdModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
@Transactional
public class BasicDAO<T> {

    @Autowired
    private SessionFactory sessionFactory;

    private Class<T> modelClass;
    private String modelName;

    public BasicDAO() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.modelClass = (Class<T>) pt.getActualTypeArguments()[0];
        this.modelName = this.modelClass.getSimpleName();
    }

    protected Session getTmpSession() {
        return this.sessionFactory.getCurrentSession();
    }

    public Integer saveBackId(T model) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        Integer id = (Integer) tmpSession.save(model);
        tmpSession.flush();
        transaction.commit();
        return id;
    }


    public void save(T model) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        tmpSession.save(model);
        tmpSession.flush();
        transaction.commit();
    }

//    public void update(T model) {
//        Session tmpSession = this.getTmpSession();
//        Transaction transaction = tmpSession.beginTransaction();
//        tmpSession.update(model);
//        tmpSession.flush();
//        transaction.commit();
//    }

    public List<T> searchAll() {
        Session tmpSession = this.getTmpSession();
        String qry = "FROM " + this.modelName;
        return tmpSession.createQuery(qry).list();
    }

    public void deleteBySomeId(Object id, String idName) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "DELETE FROM " + this.modelName +
                " WHERE " + idName + " = (:id)";
        tmpSession.createQuery(qry)
                .setParameter("id", id)
                .executeUpdate();
        transaction.commit();
    }

    public List<T> searchBySomeId(Object id, String idName) {
        System.out.println("A");
        Session tmpSession = this.getTmpSession();
        String qry = "FROM " + this.modelName +
                " WHERE " + idName + " = (:id)";
        return tmpSession.createQuery(qry)
                .setParameter("id", id)
                .list();
    }

    public void deleteBySomeId(List<Object> idArr, String idName) {
        if (idArr != null && idArr.size() != 0) {
            Session tmpSession = this.getTmpSession();
            Transaction transaction = tmpSession.beginTransaction();
            String qry = "DELETE FROM " + this.modelName +
                    " WHERE " + idName + " IN (:idArr)";
            tmpSession.createQuery(qry)
                    .setParameterList("idArr", idArr)
                    .executeUpdate();
            transaction.commit();
        }
    }

    public List<T> searchBySomeId(List<Object> idArr, String idName) {
        List<T> res;
        if (idArr != null && idArr.size() != 0) {
            Session tmpSession = this.getTmpSession();
            String qry = "FROM " + this.modelName +
                    " WHERE " + idName + " IN (:idArr)";
            res = tmpSession.createQuery(qry)
                    .setParameterList("idArr", idArr)
                    .list();
        } else {
            res = Collections.EMPTY_LIST;
        }
        return res;
    }

    public void deleteBySomeId(List<Object> idArr, String idName, List<Object> jdArr, String jdName) {
        if (idArr != null && idArr.size() != 0 && jdArr != null && jdArr.size() != 0) {
            Session tmpSession = this.getTmpSession();
            Transaction transaction = tmpSession.beginTransaction();
            String qry = "DELETE FROM " + this.modelName +
                    " WHERE " + idName + " IN (:idArr)" + " AND " + jdName + " IN (:jdArr)";
            tmpSession.createQuery(qry)
                    .setParameterList("idArr", idArr)
                    .setParameterList("jdArr", jdArr)
                    .executeUpdate();
            transaction.commit();
        }
    }

    public List<T> searchBySomeId(List<Object> idArr, String idName, List<Object> jdArr, String jdName) {
        List<T> res;
        if (idArr != null && idArr.size() != 0 && jdArr != null && jdArr.size() != 0) {
            Session tmpSession = this.getTmpSession();
            String qry = "FROM " + this.modelName +
                    " WHERE " + idName + " IN (:idArr)" + " AND " + jdName + " IN (:jdArr)";
            res = tmpSession.createQuery(qry)
                    .setParameterList("idArr", idArr)
                    .setParameterList("jdArr", jdArr)
                    .list();
        } else {
            res = Collections.EMPTY_LIST;
        }
        return res;
    }

    public void deleteBySomeId(Object id, String idName, Object jd, String jdName) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "DELETE FROM " + this.modelName +
                " WHERE " + idName + " = (:id)" + " AND " + jdName + " = (:jd)";
        tmpSession.createQuery(qry)
                .setParameter("id", id)
                .setParameter("jd", jd)
                .executeUpdate();
        transaction.commit();
    }

    public List<T> searchBySomeId(Object id, String idName, Object jd, String jdName) {
        Session tmpSession = this.getTmpSession();
        String qry = "FROM " + this.modelName +
                " WHERE " + idName + " = (:id)" + " AND " + jdName + " = (:jd)";
        return tmpSession.createQuery(qry)
                .setParameter("id", id)
                .setParameter("jd", jd)
                .list();
    }


//    public List<Object[]> searchBySomeId(Object id, String idName, String select) {
//        Session tmpSession = this.getTmpSession();
//        String qry = "SELECT " + select +
//                " FROM " + this.modelName +
//                " WHERE " + idName + " = (:id)";
//        return tmpSession.createQuery(qry)
//                .setParameter("id", id)
//                .list();
//    }
//
//
//    public List<Object[]> searchBySomeId(List<Object> idArr, String idName, String select) {
//        List<Object[]> res;
//        if (idArr != null && idArr.size() != 0) {
//            Session tmpSession = this.getTmpSession();
//            String qry = "SELECT " + select +
//                    " FROM " + this.modelName +
//                    " WHERE " + idName + " IN (:idArr)";
//            res = tmpSession.createQuery(qry)
//                    .setParameterList("idArr", idArr)
//                    .list();
//        } else {
//            res = Collections.EMPTY_LIST;
//        }
//        return res;
//    }


    public boolean isNameDuplicate(String name) {
        List<T> res;
        Session tmpSession = this.getTmpSession();
        String qry = "FROM " + this.modelName +
                " WHERE name = (:name)";
        res = tmpSession.createQuery(qry)
                .setParameter("name", name)
                .list();
        return res.size() > 0;
    }


    public boolean isNameDuplicate(String name, Object id) {
        List<T> res;
        Session tmpSession = this.getTmpSession();
        String qry = "FROM " + this.modelName +
                " WHERE id <> (:id) AND name = (:name)";
        res = tmpSession.createQuery(qry)
                .setParameter("id", id)
                .setParameter("name", name)
                .list();
        return res.size() > 0;
    }


    public boolean isNameDuplicate(List<Object> idArr, String name) {
        if (idArr != null && idArr.size() != 0) {
            List<T> res;
            Session tmpSession = this.getTmpSession();
            String qry = "FROM " + this.modelName +
                    " WHERE id IN (:idArr) AND name = (:name)";
            res = tmpSession.createQuery(qry)
                    .setParameterList("idArr", idArr)
                    .setParameter("name", name)
                    .list();
            return res.size() > 0;
        } else {
            return false;
        }
    }


    public boolean isNameDuplicate(List<Object> idArr, String name, Object id) {
        if (idArr != null && idArr.size() != 0) {
            List<T> res;
            Session tmpSession = this.getTmpSession();
            String qry = "FROM " + this.modelName +
                    " WHERE id <> (:id) AND id IN (:idArr) AND name = (:name)";
            res = tmpSession.createQuery(qry)
                    .setParameter("id", id)
                    .setParameterList("idArr", idArr)
                    .setParameter("name", name)
                    .list();
            return res.size() > 0;
        } else {
            return false;
        }
    }
}
