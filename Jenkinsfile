pipeline {
    agent { label 'master' }
    options {
        disableConcurrentBuilds()
    }
    environment {

        //Stash repository name, example: go-boilerplate 
        REPOSITORY = "Raidiam"

        }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build Test-API Project') {
            steps {
                sh """ 
                #Clone the repository thiagohcn-customer-data-api-java if not exists
                if [ ! -d "thiagohcn-customer-data-api-java" ]; then
                    git clone https://bitbucket.org/thiagohcn/customer-data-api-java.git
                fi 
                """	
                sh """ 
                #Build
                cd thiagohcn-customer-data-api-java && \
                make run-local
                """ 
            }
        }
        stage('Start Automated Tests') {
            when { 
                expression { return "${branch_name}" ==~ /^PR-(.*)/ }
            }
            failFast true
              stage('local') {
                steps {
                  build job: 'Automated Tests', parameters: [string(name: 'branch_name', value: "${CHANGE_BRANCH}"), string(name: 'command', value: 'mvn clean test -Dtest=ApiRunner -Denvironment=local')], quietPeriod: 1, wait: true
                }
              }
        }
    }
    post {
        always {
          sh """ 
            make fix-permissions
          """ 

          //Remove workspace
          cleanWs()

          script {
            office365ConnectorSend(webhookUrl: "${CHAT_URL}",
                factDefinitions: [
                    [name: "Job", template: env.JOB_NAME],
                    [name: "Build", template: currentBuild.currentResult],
                    [name: "Node", template: env.NODE_NAME],
                    [name: "Branch", template: env.GIT_BRANCH],
                    [name: "Commit", template: "[${env.GIT_COMMIT}](https://github.com/pedrohbps/${REPOSITORY}/commits/${env.GIT_COMMIT})"],
                    [name: "Console log:", template: "[${env.BUILD_NUMBER}](${env.BUILD_URL}consoleFull)"],
                ]
            )
          }
        }
    }
}
