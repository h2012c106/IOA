package com.IOA.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
@Transactional
public class DoubleQKDAO<T> extends BasicDAO<T> {
}
