#!/usr/bin/env groovy

/**
 * Run unit tests
 */
def call() {
    echo "Running unit tests..."
    
    // Run npm test if you're using npm
    try {
        sh "npm install"  // Install dependencies if needed
        sh "npm test"     // Run the unit tests
        echo "Unit tests completed successfully"
    } catch (Exception e) {
        echo "Unit tests failed: ${e.getMessage()}"
        currentBuild.result = 'FAILURE'
        throw e  // Re-throw the exception to stop the pipeline
    }
}
