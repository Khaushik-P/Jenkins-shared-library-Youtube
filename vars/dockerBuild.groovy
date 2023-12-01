def call(String dockerHubUsername,String imageName){
    sh "docker build --build-arg REACT_APP_RAPID_API_KEY=a146c3444dmsh3c1d9ef4bb01d85p13a21fjsn413b5c1c5093 -t ${imageName} ."
    sh "docker tag ${imageName} ${dockerHubUsername}/${imageName}:latest"
     withDockerRegistry([url: 'https://index.docker.io/v1/', credentialsId: 'docker']) {
        sh "docker push ${dockerHubUsername}/${imageName}:latest"
    }
}