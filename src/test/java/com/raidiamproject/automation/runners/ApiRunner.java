package com.raidiamproject.automation.runners;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.raidiamproject.automation.api.AccountsApiTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AccountsApiTest.class })

public class ApiRunner {

}
