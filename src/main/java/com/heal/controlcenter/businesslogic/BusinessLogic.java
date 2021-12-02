package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import org.springframework.stereotype.Component;

@Component
public interface BusinessLogic<T, V, R> {
    UtilityBean<T> clientValidation(T requestBody, String... requestParams) throws ClientException;
    V serverValidation(UtilityBean<T> utilityBean) throws ServerException;
    R process(V bean) throws DataProcessingException;
}
