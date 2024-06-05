package com.raidiamproject.automation.persistence.implementsdao;

import com.raidiamproject.automation.persistence.dao.AccountsDAO;
import com.raidiamproject.automation.tables.Accounts;
import com.raidiamproject.automation.utils.ConnectionDataBase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AccountsDAOImpl implements AccountsDAO {

    @Override
    public Optional<Accounts> findAccountsBy(String id) throws SQLException {
        Connection connectionBase = ConnectionDataBase.getInstance().getConnection();
        PreparedStatement prepare = null;

        try {
            prepare = connectionBase
                    .prepareStatement("SELECT ID, BANK, ACCOUNT_NUMBER WHERE ID = ?");
            prepare.setString(1, id);
            ResultSet resultSet = prepare.executeQuery();

            return resultSet.next() ? Optional.of(new Accounts(resultSet.getString("ID"), resultSet.getString("BANK"),
                    resultSet.getString("ACCOUNT_NUMBER")))
                    : Optional.empty();

        } finally {
            if (prepare != null) {
                prepare.close();
            }
            connectionBase.close();
        }
    }
}