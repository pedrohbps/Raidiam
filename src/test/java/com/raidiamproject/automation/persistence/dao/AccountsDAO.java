package com.raidiamproject.automation.persistence.dao;

import com.raidiamproject.automation.tables.Accounts;
import java.sql.SQLException;
import java.util.Optional;

public interface AccountsDAO {

    Optional<Accounts> findAccountsBy(String id) throws SQLException;
}
