pipeline {
    agent { label 'First_Slave' }
    environment {
        NEXUS_COMMON_CREDS = credentials('0981d455-e100-4f93-9faf-151ac7e29d8a')
        NEXUS_URL = 'http://192.168.13.69:8081'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
	    office365ConnectorWebhooks([[
                name: 'Jenkins',
                notifyBackToNormal: true,
                notifyFailure: true,
                notifySuccess: true,
                notifyUnstable: true,
                url: "https://healsoftwareai.webhook.office.com/webhookb2/78345e71-2972-44c4-a270-fbae82662bf1@55dca2af-e23a-4402-b9a6-8833b28a02dc/JenkinsCI/7958868126734afeb78edb01dafdcc05/6fed72e3-b7dd-422f-9075-e6d96468feb0"
            ]]
        )
    }

    parameters {
        gitParameter branchFilter: 'origin/(.*)', defaultValue: 'master', name: 'BRANCH', type: 'PT_BRANCH_TAG'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    currentBuild.displayName = "#${BUILD_NUMBER}->${params.BRANCH}"
                    currentBuild.description = "Branch: ${params.BRANCH} is used for this build"
                }
                git branch: "${params.BRANCH}", url: 'https://bitbucket.org/appsone/heal-controlcenter.git', credentialsId: "fd197b00-fd06-4632-a018-36134111e086"
            }
        }
        stage('Build') {
            steps {
                withSonarQubeEnv('sonarqube_40') {
                    sh 'mvn clean install sonar:sonar'
                }
            }
        }

        stage('Archive Builds') {
            steps {
		        sh "mv target/heal-controlcenter*.tar.gz heal-controlcenter.tar.gz"
                archiveArtifacts artifacts: 'heal-controlcenter.tar.gz', fingerprint: true
            }
        }
        stage('Docker build') {
            steps {
                sh "tar -xvf heal-controlcenter.tar.gz"
                script {
                    version = sh(
                        script: "cat heal-controlcenter/version.txt | grep -i version | cut -d '-' -f4",
                        returnStdout: true,
                    ).trim()
                }
                echo "Building project in version: ${version}"
                sh "docker build -t heal-controlcenter:${version} ."
            }
        }
        stage('Publish Docker Image') {
            steps {
                sh "docker save heal-controlcenter:${version} > heal-controlcenter_${version}.tar"
                sh "curl -v -u ${NEXUS_COMMON_CREDS} --upload-file heal-controlcenter_${version}.tar ${NEXUS_URL}/nexus/repository/tls_docker_images/heal-controlcenter_${version}.tar"
                sh "echo heal-controlcenter_${version} > /tmp/heal-controlcenter_version"
            }
            post {
                always {
                    sh "docker rmi -f heal-controlcenter:${version}"
                }
            }
        }
        stage('Cleanup') {
            steps {
                cleanWs()
            }
        }
    }
}
