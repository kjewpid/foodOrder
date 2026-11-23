
echo "Startar Docker-containrar..."
docker compose up -d --build

echo "Väntar på registrering i Eureka..."

ready=false

for ((i=1; i<=60; i++)); do
    if docker compose exec -T eureka-server \
        bash -c 'curl -fsS http://localhost:8010/eureka/apps' \
        2>/dev/null | grep -q '<name>FRONTEND</name>'; then

        if docker compose exec -T eureka-server \
            bash -c 'curl -fsS http://localhost:8010/eureka/apps' \
            2>/dev/null | grep -q '<name>USER-SERVICE</name>'; then

            ready=true
            break
        fi
    fi

    sleep 2
done

if [ "$ready" = false ]; then
    echo "Tjänsterna registrerades inte i Eureka inom 120 sekunder"
    exit 1
fi

echo "Tjänsterna är registrerade"

echo "Startar om API Gateway..."
docker compose restart api-gateway

echo "FoodOrder är startat!"
echo "Webbplats: http://localhost"
echo "Eureka: http://localhost:8010"
