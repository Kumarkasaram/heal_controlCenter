package com.heal.controlcenter.util;

import com.heal.controlcenter.beans.AccountBean;
import com.heal.controlcenter.dao.mysql.AccountsDao;
import com.heal.controlcenter.exception.ControlCenterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class ValidationsUtils {

    @Autowired
    AccountsDao accountsDao;

    private ValidationsUtils() {
    }

    public AccountBean validAndGetAccount(String identifier) throws ControlCenterException {
        List<AccountBean> accounts = accountsDao.getAccounts();
        if (accounts == null || accounts.isEmpty()) return null;
        Optional<AccountBean> account = accounts
                .stream()
                .filter(it -> it.getIdentifier().equals(identifier))
                .findAny();

        return account.orElse(null);
    }

}
