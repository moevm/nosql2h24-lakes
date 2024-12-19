
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

    const map = L.map("map").setView([60.0, 30.0], 7); // Центр карты
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png").addTo(map);

    let drawnRectangle;
    map.on("click", (e) => {
        if (drawnRectangle) map.removeLayer(drawnRectangle);
        const bounds = [[e.latlng.lat - 0.1, e.latlng.lng - 0.1], [e.latlng.lat + 0.1, e.latlng.lng + 0.1]];
        drawnRectangle = L.rectangle(bounds, { color: "blue", weight: 1 }).addTo(map);
    });

    document.getElementById("searchByArea").addEventListener("click", () => {
        if (drawnRectangle) {
            const bounds = drawnRectangle.getBounds();
            const area = {
                north: bounds.getNorth(),
                south: bounds.getSouth(),
                east: bounds.getEast(),
                west: bounds.getWest(),
            };
            fetch("/api/lakes/searchByArea", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(area),
            })
                .then((response) => response.json())
                .then((data) => {
                    console.log("Найденные озера:", data);
                })
                .catch((error) => console.error("Ошибка поиска:", error));
        } else {
            alert("Выделите область на карте.");
        }
    });
});

