#!/usr/bin/env groovy

/**
 * Update Kubernetes manifests with new image tags
 */
def call(Map config = [:]) {
    def imageTag = config.imageTag ?: error("Image tag is required")
    def manifestsPath = config.manifestsPath ?: 'kubernetes'
    def gitCredentials = config.gitCredentials ?: 'github-credentials'
    def gitUserName = config.gitUserName ?: 'Jenkins CI'
    def gitUserEmail = config.gitUserEmail ?: 'jenkins@example.com'
    
    echo "Updating Kubernetes manifests with image tag: ${imageTag}"
    
    withCredentials([usernamePassword(
        credentialsId: gitCredentials,
        usernameVariable: 'GIT_USERNAME',
        passwordVariable: 'GIT_PASSWORD'
    )]) {
        // Configure Git
        sh """
            git config user.name "${gitUserName}"
            git config user.email "${gitUserEmail}"
        """
        
        // Update deployment manifests with new image tags - using your DockerHub username
        sh """
            # Update main application deployment - changed to divyasatpute/easyshop-app
            sed -i "s|image: .*/easyshop-app:.*|image: divyasatpute/easyshop-app:${imageTag}|g" ${manifestsPath}/08-easyshop-deployment.yaml
            
            # Update migration job if it exists - changed to divyasatpute/easyshop-migration
            if [ -f "${manifestsPath}/12-migration-job.yaml" ]; then
                sed -i "s|image: .*/easyshop-migration:.*|image: divyasatpute/easyshop-migration:${imageTag}|g" ${manifestsPath}/12-migration-job.yaml
            fi
            
            # Ensure ingress is using the correct domain (can be updated later if needed)
            if [ -f "${manifestsPath}/10-ingress.yaml" ]; then
                sed -i "s|host: .*|host: easyshop.letsdeployit.com|g" ${manifestsPath}/10-ingress.yaml
            fi
            
            # Check for changes
            if git diff --quiet; then
                echo "No changes to commit"
            else
                # Commit and push changes
                git add ${manifestsPath}/*.yaml
                git commit -m "Update image tags to ${imageTag} and ensure correct domain [ci skip]"
                
                # Push to your GitHub repo - update with your repo name if different
                git remote set-url origin https://\${GIT_USERNAME}:\${GIT_PASSWORD}@github.com/divyasatpute/EasyShop-KIND.git
                git push origin HEAD:\${GIT_BRANCH}
            fi
        """
    }
}
