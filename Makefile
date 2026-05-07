.PHONY: setup start-api start-ui stop logs

setup:
	@echo "Setting up both projects..."
	$(MAKE) -C schedulling-api setup
	$(MAKE) -C schedulling-ui setup

start-api:
	@echo "Starting API..."
	$(MAKE) -C schedulling-api start

start-ui:
	@echo "Starting UI..."
	$(MAKE) -C schedulling-ui start

stop:
	@echo "Stopping everything..."
	$(MAKE) -C schedulling-api stop
	$(MAKE) -C schedulling-ui stop

logs:
	$(MAKE) -C schedulling-api logs
