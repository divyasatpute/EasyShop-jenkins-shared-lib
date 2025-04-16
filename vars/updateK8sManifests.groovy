def call(Map config) {
    def imageTag = config.imageTag
    def manifestsPath = config.manifestsPath
    def gitCredentials = config.gitCredentials
    def gitUserName = config.gitUserName
    def gitUserEmail = config.gitUserEmail

    dir(manifestsPath) {
        // Set Git user config
        sh "git config user.name '${gitUserName}'"
        sh "git config user.email '${gitUserEmail}'"

        // Update the deployment image tag
        sh """
        sed -i 's|image: .*|image: ${config.imageTag}|' deployment.yaml
        """

        // Commit and push the changes
        withCredentials([usernamePassword(credentialsId: gitCredentials, passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
            sh """
            git add .
            git commit -m 'Update K8s manifests with tag ${imageTag}' || echo 'No changes to commit'
            git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/divyasatpute/tws-e-commerce-app.git HEAD:${env.GIT_BRANCH}
            """
        }
    }
}
