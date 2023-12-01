def COLOR_MAP=[
    'FAILURE':'danger',
    'SUCCESS':'good'
]

pipeline{
    
    agent any 
      parameters{
        choice(name:'action' , choices:'create\ndelete' ,description:'Select create or destroy.')
    }
    environment{
        SCANNER_HOME=tool 'sonar-scanner'
    }
    stages {
        stage('Clean Workspace'){
            steps{
            cleanWs()
            }
        }
        
        stage('Git Checkout'){
            
            steps{
                
                script{
                    
                    git branch: 'main', url: 'https://github.com/Khaushik-P/Youtube-app-Devops.git'
                }
            }
        }
        stage('Static code analysis'){
            when { expression { params.action == 'create'}}    
            steps{
                
                script{
                    
                    withSonarQubeEnv(credentialsId: 'sonar-api') {
                        
                      sh ''' $SCANNER_HOME/bin/sonar-scanner -Dsonar.projectName=Youtube1 -Dsonar.projectKey=Youtube1 '''
                    }
                   }
                    
                }
            }
            stage('Quality Gate Status'){
                when { expression { params.action == 'create'}}    
                steps{
                    
                    script{
                        
                        waitForQualityGate abortPipeline: false, credentialsId: 'sonar-api'
                    }
                }
            }
            stage('npm install'){
              when { expression { params.action == 'create'}}    
                steps{
                    sh 'npm install'
                    }
                }
        //     stage('OWASP FS SCAN'){
        //         when { expression { params.action == 'create'}}
        //      steps{
        //          dependencyCheck additionalArguments: '--scan ./ --disableYarnAudit --disableNodeAudit', odcInstallation: 'DP-Check'
        //         dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
        //     }
        // }
        stage('TRIVY FS SCAN') {
            when { expression { params.action == 'create'}}
            steps {
                sh "trivy fs . > trivyfs.txt"
            }
        }
        stage("Docker Build & Push"){
            when { expression { params.action == 'create'}}
            steps{
                script{
                       withDockerRegistry(credentialsId: 'docker', toolName: 'docker'){   
                       sh "docker build --build-arg REACT_APP_RAPID_API_KEY=a146c3444dmsh3c1d9ef4bb01d85p13a21fjsn413b5c1c5093 -t youtube ."
                       sh "docker tag youtube khaushik/youtube:latest "
                       sh "docker push khaushik/youtube:latest "
                      }
                }
            }
        }
        stage("TRIVY"){
            when { expression { params.action == 'create'}}
            steps{
                sh "trivy image khaushik/youtube:latest > trivyimage.txt" 
            }
        }
        stage('Deploy to container'){
            when { expression { params.action == 'create'}}
            steps{
                sh 'docker run -d --name youtube1 -p 3000:3000 khaushik/youtube:latest'
            }
        }
        stage('Delete Container'){
            when { expression { params.action == 'delete'}}
            steps{
                 sh 'docker stop youtube1'
                 sh 'docker rm youtube1'
            }
        }

        // stage('Deploy to kubernets'){
        //     steps{
        //         script{
        //             withKubeConfig(caCertificate: '', clusterName: '', contextName: '', credentialsId: 'k8s', namespace: '', restrictKubeConfigAccess: false, serverUrl: '') {
        //                 sh 'kubectl apply -f deployment.yml'
        //             }
        //         }
        //     }
        // }

          
    }

                
         post {
    always {
        echo 'Slack Notifications'
        slackSend (
            channel: '#jenkins', 
            color: COLOR_MAP[currentBuild.currentResult],
            message: "*${currentBuild.currentResult}:* Job ${env.JOB_NAME} \n build ${env.BUILD_NUMBER} \n More info at: ${env.BUILD_URL}"
        )
    }
}
}
