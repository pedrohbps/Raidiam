package com.raidiamproject.automation.runners;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.raidiamproject.automation.api.GetAccountEndpointTest;
import com.raidiamproject.automation.api.ListAccountsEndpointTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ListAccountsEndpointTest.class,GetAccountEndpointTest.class })

public class ApiRunner {

}
