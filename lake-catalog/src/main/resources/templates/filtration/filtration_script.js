document.addEventListener("DOMContentLoaded", () => {
    const searchButton = document.getElementById("searchButton");
    const returnButton = document.getElementById("returnButton");
    const applyFiltersButton = document.getElementById("applyFilters");

    const depthRange = document.getElementById("depthRange");
    const depthInput = document.getElementById("depthInput");
    const areaRange = document.getElementById("areaRange");
    const areaInput = document.getElementById("areaInput");
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
    depthRange.addEventListener("input", () => {
        depthInput.value = depthRange.value;
    });

    depthInput.addEventListener("input", () => {
        // Проверка, чтобы значение было в допустимых пределах
        if (depthInput.value < depthRange.min) {
            depthInput.value = depthRange.min;
        } else if (depthInput.value > depthRange.max) {
            depthInput.value = depthRange.max;
        }
        depthRange.value = depthInput.value;
    });

    // Обработчик для синхронизации значения площади (range и input)
    areaRange.addEventListener("input", () => {
        areaInput.value = areaRange.value;
    });

    areaInput.addEventListener("input", () => {
        // Проверка, чтобы значение было в допустимых пределах
        if (areaInput.value < areaRange.min) {
            areaInput.value = areaRange.min;
        } else if (areaInput.value > areaRange.max) {
            areaInput.value = areaRange.max;
        }
        areaRange.value = areaInput.value;
    });

    // Обработчик кнопки поиска
    // searchButton.addEventListener("click", () => {
    //     const searchQuery = document.getElementById("searchInput").value;
    //     alert(`Ищем: ${searchQuery}`);
    // });

    // Обработчик кнопки применения фильтров
    searchButton.addEventListener("click", () => {
        const region = document.getElementById("regionSelect").value;
        const city = document.getElementById("citySelect").value;
        const depth = depthRange.value;
        const area = areaRange.value;

        alert(`Фильтры применены:\nРегион: ${region}\nГород: ${city}\nГлубина: ${depth} м\nПлощадь: ${area} км²`);
    });
    returnButton.addEventListener("click", () => {
        window.location.href = '/main';
    });
});

