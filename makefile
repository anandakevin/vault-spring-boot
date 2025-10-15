SUBDIR       ?= simple-crud-logging-gateway
BUILD_CONTEXT ?= $(SUBDIR)
DOCKERFILE    ?= $(SUBDIR)/Dockerfile

REGISTRY   ?=
REPOSITORY ?= vault-spring-boot
ENV ?= LOCAL

LOCAL_ARCH := $(shell uname -m)

ifeq ($(ENV),LOCAL)
  PLATFORM =
  IMAGE_TAG ?= latest-$(LOCAL_ARCH)
else
  PLATFORM = linux/amd64
  IMAGE_TAG ?= ci-latest
endif

MVN = mvn -f $(SUBDIR)/pom.xml

docker-build-push:
	docker build \
		$(if $(PLATFORM),--platform $(PLATFORM)) \
		--build-arg ENV=$(ENV) \
		-f $(DOCKERFILE) \
		$(if $(REGISTRY),--tag $(REGISTRY)/$(REPOSITORY):$(IMAGE_TAG)) \
		--tag $(REPOSITORY):$(IMAGE_TAG) \
		$(BUILD_CONTEXT)
	$(if $(REGISTRY),docker push $(REGISTRY)/$(REPOSITORY):$(IMAGE_TAG),)

docker-build-local:       ; $(MAKE) docker-build-push ENV=LOCAL
docker-build-push-dev:    ; $(MAKE) docker-build-push ENV=DEV  REGISTRY=$(REGISTRY)
docker-build-push-prod:   ; $(MAKE) docker-build-push ENV=PROD REGISTRY=$(REGISTRY)

clean:    ; $(MVN) clean
compile:  ; $(MVN) compile
package:  ; $(MVN) -DskipTests=true clean package
run:      ; $(MVN) spring-boot:run
test:     ; $(MVN) test
