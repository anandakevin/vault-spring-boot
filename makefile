# Variables
PROJECT_NAME = vault-spring-boot
REGISTRY ?=
REPOSITORY ?= vault-spring-boot
ENV ?= LOCAL  # Allow ENV to be overridden (defaults to LOCAL)

# Detect the architecture (local vs CI)
LOCAL_ARCH := $(shell uname -m)

# Set PLATFORM and IMAGE_TAG based on environment (local or CI)
ifeq ($(ENV),)
    ENV := DEV  # Default to DEV if ENV is not set
endif

# Set platform based on environment and architecture
ifeq ($(ENV), LOCAL)
    PLATFORM =      # No platform defined for local (will be handled by Docker default)
    IMAGE_TAG := latest-$(LOCAL_ARCH)  # Tag based on local architecture
else
    PLATFORM = linux/amd64  # For DEV/PROD, use amd64 platform
    IMAGE_TAG := $(IMAGE_TAG)  # Tag based on amd64 architecture for CI
endif

# Build, tag, and push the Docker image
docker-build-push:
	docker build \
		$(if $(PLATFORM),--platform $(PLATFORM)) \
		--build-arg ENV=$(ENV) \
		$(if $(REGISTRY),--tag $(REGISTRY)/$(REPOSITORY):$(IMAGE_TAG)) \
		--tag $(REPOSITORY):$(IMAGE_TAG) .  # Tag without REGISTRY for local
	$(if $(REGISTRY),docker push $(REGISTRY)/$(REPOSITORY):$(IMAGE_TAG),)  # Push only if REGISTRY is set

# Local build target (for ARM64 architecture or default local configuration)
docker-build-local:
	$(MAKE) docker-build-push ENV=LOCAL

# DEV environment build (with push)	
docker-build-push-dev:
	$(MAKE) docker-build-push ENV=DEV REGISTRY=$(REGISTRY)

# PROD environment build (with push)
docker-build-push-prod:
	$(MAKE) docker-build-push ENV=PROD REGISTRY=$(REGISTRY)

# Define modules to build or run
MODULES = SpringAutowiring SpringAutowiringAnnotation SpringHelloWorld

# Commands to clean, compile, and run the Spring Boot app
clean:
	mvn clean

compile:
	mvn compile

package:
	mvn clean package

run:
	mvn spring-boot:run

test:
	mvn test

# Run all modules specified in the MODULES variable
run-specific-modules:
	mvn spring-boot:run -pl $(MODULES)

# Run a specific module (example: module-a)
run-module-SpringHelloWorld:
	mvn spring-boot:run -pl SpringHelloWorld

# Test a specific module (example: module-a)
test-module-SpringHelloWorld:
	mvn test -pl SpringHelloWorld
