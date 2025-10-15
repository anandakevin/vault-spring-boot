# How to run

There are three profiles provided in this simple gateway example.

1. Forward
   This forwards to `httpbin.org/get`
   Powershell: `mvn spring-boot:run "-Dspring-boot.run.profiles=forward"  `
   CMD & Bash: `mvn spring-boot:run -Dspring-boot.run.profiles=forward `
   Test it: `curl -i http://localhost:8080/bin/get`
2. Rewrite
   This rewrites the URL parameter to `httpbin.org/anything/foo/bar`
   Powershell: `mvn spring-boot:run "-Dspring-boot.run.profiles=rewrite"  `
   CMD & Bash: `mvn spring-boot:run -Dspring-boot.run.profiles=rewrite `
   Test it: `curl -i http://localhost:8080/bin/demo/path`
3. Aggregate
   This aggregates JSON result from `httpbin.org/get` and `httpbin.org/headers`.
   Powershell: `mvn spring-boot:run "-Dspring-boot.run.profiles=aggregate"  `
   CMD & Bash: `mvn spring-boot:run -Dspring-boot.run.profiles=aggregate`
   Test it: `curl -i http://localhost:8080/aggregate | jq .`
