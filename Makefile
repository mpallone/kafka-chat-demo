jar:
	mvn clean install

image:
	docker build -t demo:1.0-SNAPSHOT .

build: jar image

run: clean
	docker-compose up -d
	docker-compose logs -f demo

clean:
	docker-compose down

producer:
	docker exec -it kafka kafka-console-producer.sh --broker-list PLAINTEXT://localhost:9090 --bootstrap-server localhost:9090 --topic message-queue

consumer:
	docker exec -it kafka kafka-console-consumer.sh --bootstrap-server localhost:9090 --topic message-queue --from-beginning

topics:
	docker exec -t kafka kafka-topics.sh --bootstrap-server localhost:9090 --list

.PHONY: jar image build run clean producer consumer topics
