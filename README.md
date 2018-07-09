# jfrog-gradle-configuration
Reproducer for issue with jfrog plugin while resolving dependencies under kapt configuration.  
Present use case is a kotlin project which uses the [owasp dependencycheck gradle plugin](https://plugins.gradle.org/plugin/org.owasp.dependencycheck)
which fails under a gradle configuration (kapt).
## Issue
Running the following fails :  
```./gradlew clean dependencyCheckAnalyze -i```  
with stacktrace  
```
FAILURE: Build failed with an exception.   
   * What went wrong:
   Execution failed for task ':dependencyCheckAnalyze'.
   > Could not resolve all dependencies for configuration ':kapt'.
      > Cannot resolve external dependency org.jetbrains.kotlin:kotlin-annotation-processing-gradle:1.2.51 because no repositories are defined.
        Required by:
            project :
  ```
            
And actually adding the repository manually solves the issue like :
```
repositories {
    maven {
        url = URI("https://my-artifactory-uri")
        credentials {
            username = "user"
            password = "password"
        }
    }
}
```
## Cause
The issue seems to be caused by the jfrog plugin not resolving plugin dependencies for this configuration and I haven't found a way in options to enable this.