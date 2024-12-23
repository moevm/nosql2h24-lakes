// Инициализация карты
const map = L.map('map', {
    zoomControl: false
}).setView([51.505, -0.09], 13);

L.control.zoom({ position: 'bottomright' }).addTo(map);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '© OpenStreetMap'
}).addTo(map);

let currentMarker = null;
let routeControl = null;
let userLocation = null;

// Получение геолокации пользователя
function getUserLocation() {
    if (!navigator.geolocation) {
        alert('Геолокация не поддерживается вашим браузером.');
        return;
    }

    navigator.geolocation.getCurrentPosition(
        (position) => {
            userLocation = {
                lat: position.coords.latitude,
                lon: position.coords.longitude
            };
        },
        (error) => {
            console.error('Ошибка получения геолокации:', error);
            alert('Не удалось получить ваше местоположение.');
        }
    );
}

// Поиск объектов
async function searchOnMap() {
    const query = document.getElementById('searchQuery').value;
    if (!query) {
        alert('Введите запрос для поиска!');
        return;
    }

    try {
        const response = await fetch(`https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(query)}&format=json&addressdetails=1`);
        const results = await response.json();

        if (results.length === 0) {
            alert('Ничего не найдено!');
            return;
        }

        const { lat, lon, display_name } = results[0];
        map.setView([lat, lon], 15);

        if (currentMarker) {
            map.removeLayer(currentMarker);
        }

        currentMarker = L.marker([lat, lon]).addTo(map);
        currentMarker.bindPopup(display_name).openPopup();

        document.getElementById('locationInfo').innerText = `Выбранный объект: ${display_name}`;
        document.getElementById('routeEnd').value = display_name; // Автозаполнение конечного адреса
        document.getElementById('step2').classList.remove('hidden'); // Отобразить шаг 2

        document.getElementById('sidebar').classList.add('showBackground');
    } catch (error) {
        console.error('Ошибка при поиске:', error);
        alert('Произошла ошибка при поиске.');
    }
}

// Отображение полей маршрута
function showRouteInputs() {
    goToStep(3);

    // Заполнение значений
    if (userLocation) {
        document.getElementById('routeStart').value = 'Ваше местоположение';
    }
    document.getElementById('routeEnd').value = document.getElementById('searchQuery').value;
}

// Очистка полей маршрута
function clearRouteInputs() {
    document.getElementById('routeStart').value = '';
    document.getElementById('routeEnd').value = '';
    goToStep(2);

    // Убираем контейнер маршрута
    if (routeControl) {
        map.removeControl(routeControl);
        routeControl = null;

        const routeContainer = document.querySelector('.leaflet-routing-container');
        if (routeContainer) routeContainer.remove();
    }
}

// Построение маршрута
async function buildRoute() {
    const startAddress = document.getElementById('routeStart').value;
    const endAddress = document.getElementById('routeEnd').value;

    if (!startAddress || !endAddress) {
        alert('Заполните оба поля!');
        return;
    }

    try {
        const geocode = async (address) => {
            const response = await fetch(
                `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(address)}&format=json`
            );
            const data = await response.json();
            if (data.length === 0) {
                throw new Error(`Адрес "${address}" не найден`);
            }
            return { lat: parseFloat(data[0].lat), lon: parseFloat(data[0].lon) };
        };

        const startCoords = userLocation
            ? userLocation
            : await geocode(startAddress);
        const endCoords = await geocode(endAddress);

        if (routeControl) {
            map.removeControl(routeControl);
        }

        routeControl = L.Routing.control({
            waypoints: [
                L.latLng(startCoords.lat, startCoords.lon),
                L.latLng(endCoords.lat, endCoords.lon),
            ],
            routeWhileDragging: true,
        }).addTo(map);

        // Перемещение маршрута в сайдбар
        const routeContainer = document.querySelector('.leaflet-routing-container');
        const sidebar = document.getElementById('sidebar');
        const buildRouteButton = document.getElementById('buildRouteButton');

        if (routeContainer && sidebar && buildRouteButton) {
            routeContainer.style.position = 'static';
            routeContainer.style.marginTop = '10px';
            buildRouteButton.insertAdjacentElement('afterend', routeContainer);
        }

        map.fitBounds([
            [startCoords.lat, startCoords.lon],
            [endCoords.lat, endCoords.lon],
        ]);
    } catch (error) {
        alert(`Ошибка: ${error.message}`);
        console.error(error);
    }
}

// Переход между шагами
function goToStep(step) {
    document.querySelectorAll('#searchContainer, #step2, #routeContainer').forEach((el) => el.classList.add('hidden'));
    if (step === 1) {
        document.getElementById('searchContainer').classList.remove('hidden');
    } else if (step === 2) {
        document.getElementById('step2').classList.remove('hidden');
        document.getElementById('searchContainer').classList.remove('hidden');
    } else if (step === 3) {
        document.getElementById('routeContainer').classList.remove('hidden');
    }
}

// Сброс поиска
function resetSearch() {
    document.getElementById('searchQuery').value = '';
    map.setView([55.7558, 37.6173], 12);

    if (currentMarker) {
        map.removeLayer(currentMarker);
    }

    document.getElementById('sidebar').classList.remove('showBackground');
    goToStep(1);
}

// Инициализация
getUserLocation();
