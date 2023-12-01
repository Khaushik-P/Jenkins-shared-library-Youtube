@Library('Jenkins-shared-library1') _

def COLOR_MAP=[
    'FAILURE':'danger',
    'SUCCESS':'good'
]

pipeline{
    agent any

    parameters{
        choice(name:'action' , choices:'create\ndelete' ,description:'Select create or destroy.')
        string(name:'DOCKER_HUB_USERNAME',defaultValue:'khaushik14',description:'Docker hub username')
        string(name:'IMAGE_NAME',defaultValue:'youtube',description:'Docker image name')
    }
    tools{
        jdk 'jdk17'
        nodejs 'node16'
    }
    environment {
        SCANNER_HOME=tool 'sonar-scanner'
    }
    stages{
        stage('Clean Workspace'){
            steps{
            cleanWorkspace()
            }
        }
        stage('Git Checkout'){
            steps{
            gitCheckout('https://github.com/Aj7Ay/Youtube-clone-app.git', 'main')
            }
        }
        // stage('sonarqube Analysis'){
        // when { expression { params.action == 'create'}}    
        //     steps{
        //         sonarqubeAnalysis()
        //     }
        // }
        // stage('sonarqube QualityGate'){
        //      when { expression{params.action='create'}}
        //      steps{
        //         script{
        //             def credentialsId = 'Sonar-token'
        //             qualityGate(credentialsId)
        //         }
        //      }
        // }
        stage('Npm'){
            when{ expression {params.action='create'}}
            steps{
                npmInstall()
            }
        }
        stage('Trivy File Scan'){
            when { expression { params.action='create'}}
            steps{
                trivyFs()
            }
        }    
        stage('OWASP FS SCAN'){
            steps{
                 dependencyCheck additionalArguments: '--scan ./ --disableYarnAudit --disableNodeAudit', odcInstallation: 'DP-Check'
                dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
            }
        }
        stage('Docker Build'){
                when{expression{params.action='create'}}
                steps{
                    script{
                        def dockerHubUsername=params.DOCKER_HUB_USERNAME
                        def imageName=params.IMAGE_NAME

                        dockerBuild(dockerHubUsername,imageName)
                    }
                }
        }

        stage('Trivy Scan'){
            when{expression{params.action='create'}}
            steps{
                trivyImage()
            }
        }
        stage('Run Container'){
            when{expression{params.action='create'}}
            steps{
                runContainer()
            }
        }
         stage('Remove container'){
        when { expression { params.action == 'delete'}}    
            steps{
                removeContainer()
            }
        }
         stage('Kube deploy'){
        when { expression { params.action == 'create'}}    
            steps{
                kubeDeploy()
            }
        }
        stage('Kube delete'){
        when { expression { params.action == 'delete'}}    
            steps{
                kubeDelete()
            }
        }
    }

     post {
    always {
        echo 'Slack Notifications'
        slackSend (
            channel: '#jenkins-youtube', 
            color: COLOR_MAP[currentBuild.currentResult],
            message: "*${currentBuild.currentResult}:* Job ${env.JOB_NAME} \n build ${env.BUILD_NUMBER} \n More info at: ${env.BUILD_URL}"
        )
    }
}
}
