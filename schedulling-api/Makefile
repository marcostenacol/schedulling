.PHONY: setup start stop logs test

setup:
	@echo "Setting up Java Spring Boot API..."
	# mvn clean install

start:
	@echo "Starting Java Spring Boot API..."
	# docker-compose up -d postgres redis
	# mvn spring-boot:run

stop:
	@echo "Stopping Java Spring Boot API..."
	# docker-compose down

logs:
	# docker-compose logs -f

test:
	@echo "Running tests..."
	# mvn test
