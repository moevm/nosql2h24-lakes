
document.addEventListener("DOMContentLoaded", () => {
    const searchButton = document.getElementById("searchButton");
    const returnButton = document.getElementById("returnButton");
    const applyFiltersButton = document.getElementById("applyFilters");

    const depthInputMin = document.getElementById("depthInputMin");
    const depthInputMax = document.getElementById("depthInputMax");
    const areaInputMin = document.getElementById("areaInputMin");
    const areaInputMax = document.getElementById("areaInputMax");
    const regionInput = document.getElementById("regionInput");
    const regionList = document.getElementById("regionList");
    const regionSearch = document.getElementById("regionSearch");
    const cityInput = document.getElementById("cityInput");
    const cityList = document.getElementById("cityList");
    const citySearch = document.getElementById("citySearch");

    // Функция для открытия и закрытия выпадающего списка
    function toggleDropdown(input, list) {
        input.parentElement.classList.toggle("open");
        list.classList.toggle("show");
    }

    // Функция для фильтрации списка по поиску
    function filterList(searchInput, container) {
        const filter = searchInput.value.toLowerCase();
        const labels = container.querySelectorAll("label");
        labels.forEach(label => {
            const text = label.textContent.toLowerCase();
            label.style.display = text.includes(filter) ? "block" : "none";
        });
    }

    // Функция для обновления текста в инпуте на основании выбранных чекбоксов
    function updateInputText(input, container) {
        const selected = Array.from(container.querySelectorAll("input[type='checkbox']:checked"))
            .map(checkbox => checkbox.value);
        input.value = selected.join(", ");
    }

    // Обработчики для региона
    regionInput.addEventListener("click", () => toggleDropdown(regionInput, regionList));
    regionSearch.addEventListener("input", () => filterList(regionSearch, regionList));
    regionList.addEventListener("change", () => updateInputText(regionInput, regionList));

    // Обработчики для города
    cityInput.addEventListener("click", () => toggleDropdown(cityInput, cityList));
    citySearch.addEventListener("input", () => filterList(citySearch, cityList));
    cityList.addEventListener("change", () => updateInputText(cityInput, cityList));

    // Закрытие выпадающих списков при клике вне их области
    document.addEventListener("click", (event) => {
        if (!regionInput.parentElement.contains(event.target)) {
            regionInput.parentElement.classList.remove("open");
        }
        if (!cityInput.parentElement.contains(event.target)) {
            cityInput.parentElement.classList.remove("open");
        }
    });
    // Обработчик для синхронизации значения глубины (range и input)
    // depthRange.addEventListener("input", () => {
    //     depthInput.value = depthRange.value;
    // });

    // depthInput.addEventListener("input", () => {
    //     // Проверка, чтобы значение было в допустимых пределах
    //     if (depthInput.value < depthRange.min) {
    //         depthInput.value = depthRange.min;
    //     } else if (depthInput.value > depthRange.max) {
    //         depthInput.value = depthRange.max;
    //     }
    //     depthRange.value = depthInput.value;
    // });

    // Обработчик для синхронизации значения площади (range и input)
    // areaRange.addEventListener("input", () => {
    //     areaInput.value = areaRange.value;
    // });

    // areaInput.addEventListener("input", () => {
    //     // Проверка, чтобы значение было в допустимых пределах
    //     if (areaInput.value < areaRange.min) {
    //         areaInput.value = areaRange.min;
    //     } else if (areaInput.value > areaRange.max) {
    //         areaInput.value = areaRange.max;
    //     }
    //     areaRange.value = areaInput.value;
    // });

    function validateRange(minInput, maxInput) {
        const minValue = parseFloat(minInput.value);
        const maxValue = parseFloat(maxInput.value);

        if (minValue > maxValue) {
            maxInput.setCustomValidity("Максимальное значение должно быть больше минимального.");
        } else {
            maxInput.setCustomValidity("");
        }
    }

    // Обработчики для валидации
    depthInputMin.addEventListener("input", () => validateRange(depthInputMin, depthInputMax));
    depthInputMax.addEventListener("input", () => validateRange(depthInputMin, depthInputMax));

    areaInputMin.addEventListener("input", () => validateRange(areaInputMin, areaInputMax));
    areaInputMax.addEventListener("input", () => validateRange(areaInputMin, areaInputMax));

    // Обработчик кнопки поиска
    // searchButton.addEventListener("click", () => {
    //     const searchQuery = document.getElementById("searchInput").value;
    //     alert(`Ищем: ${searchQuery}`);
    // });

    // Обработчик кнопки применения фильтров
    searchButton.addEventListener("click", () => {
        const form = document.createElement('form');
        form.method = "GET";
        form.action = "/lakes/main";

        const addInput = (name, value) => {
            if (value) {
                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = name;
                input.value = value;
                form.appendChild(input);
            }
        };

        addInput('min_depth', document.getElementById('depthInputMin').value);
        addInput('max_depth', document.getElementById('depthInputMax').value);
        addInput('region', Array.from(document.querySelectorAll('#regionList input:checked')).map(input => input.value).join(','));
        addInput('min_square', document.getElementById('areaInputMin').value);
        addInput('max_square', document.getElementById('areaInputMax').value);
        addInput('rating', Array.from(document.querySelectorAll('.rating input:checked')).map(input => input.id).join(','));
        addInput('city', Array.from(document.querySelectorAll('#cityList input:checked')).map(input => input.value).join(','));
        addInput('name', document.getElementById('searchInput').value.trim());

        document.body.appendChild(form);
        form.submit();
    });

    returnButton.addEventListener("click", () => {
        window.location.href = '/main';
    });

    // =======================Поиск по области на карте====================
const mapElement = document.getElementById("map");
const fullscreenControls = document.getElementById("fullscreenControls");
const confirmSelectionButton = document.getElementById("confirmSelection");
const exitFullscreenButton = document.getElementById("exitFullscreen");

// Инициализация карты Leaflet
const map = L.map("map").setView([60.0, 30.0], 6);
L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    maxZoom: 19,
}).addTo(map);

// Инициализация Leaflet Draw
const drawnItems = new L.FeatureGroup();
map.addLayer(drawnItems);

const drawControl = new L.Control.Draw({
    edit: {
        featureGroup: drawnItems,
    },
    draw: {
        polygon: false,
        polyline: false,
        rectangle: true,
        circle: false,
        marker: false,
        circlemarker: false,
    },
});
map.addControl(drawControl);

// Массив для хранения названий найденных озер
const lakeNames = [];

// Функция для выполнения запроса к Overpass API
const fetchLakesInArea = async (bounds) => {
    const [south, west, north, east] = bounds;
    const query = `
        [out:json];
        (
          way["natural"="water"]["water"="lake"](${south},${west},${north},${east});
          relation["natural"="water"]["water"="lake"](${south},${west},${north},${east});
        );
        out tags;
    `;
    const url = `https://overpass-api.de/api/interpreter?data=${encodeURIComponent(query)}`;

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Ошибка запроса: ${response.status}`);
        }
        const data = await response.json();
        const lakes = data.elements;

        // Очистка текущего массива и добавление названий озер
        lakeNames.length = 0;
        lakes.forEach((lake) => {
            if (lake.tags && lake.tags.name) {
                lakeNames.push(lake.tags.name);
            }
        });

        console.log("Найденные озера:", lakeNames);
        alert(`Найдено ${lakeNames.length} озер. Смотрите консоль для деталей.`);
    } catch (error) {
        console.error("Ошибка при запросе озер:", error);
        alert("Произошла ошибка при поиске озер. Проверьте консоль для деталей.");
    }
};

// Обработчик завершения рисования
map.on(L.Draw.Event.CREATED, (event) => {
    const layer = event.layer;
    drawnItems.addLayer(layer);

    // Выделение области цветом
    layer.setStyle({
        color: "#00ff00", // Цвет выделенной области
        fillOpacity: 0.5, // Прозрачность заливки
    });

    // Получение координат области (юг, запад, север, восток)
    const bounds = layer.getBounds();
    const south = bounds.getSouth();
    const west = bounds.getWest();
    const north = bounds.getNorth();
    const east = bounds.getEast();

    // Поиск озер в выделенной области
    fetchLakesInArea([south, west, north, east]);
});

// Обработчик нажатия на карту для перехода в полноэкранный режим
mapElement.addEventListener("click", () => {
    mapElement.classList.add("fullscreen");
    fullscreenControls.classList.add("visible");
});

// Обработчик кнопки "Закрыть"
exitFullscreenButton.addEventListener("click", () => {
    mapElement.classList.remove("fullscreen");
    fullscreenControls.classList.remove("visible");
});

// Обработчик кнопки "ОК"
confirmSelectionButton.addEventListener("click", () => {
    alert("Выбор области подтвержден!");
    mapElement.classList.remove("fullscreen");
    fullscreenControls.classList.remove("visible");
});

    

// =========================================================================

});

