// Функция для обработки кнопки "Скачать"
document.getElementById('export-button').addEventListener('click', function () {
    alert('База данных будет скачана!');
    // Здесь можно добавить логику для экспорта данных (например, через API)
});

// Функция для обработки кнопки "Загрузить"
document.getElementById('upload-button').addEventListener('click', function () {
    const fileInput = document.getElementById('file-upload');
    if (fileInput.files.length === 0) {
        alert('Пожалуйста, выберите файл для загрузки');
        return;
    }
    alert(`Файл ${fileInput.files[0].name} будет загружен!`);
    // Здесь можно добавить логику для загрузки данных (например, через API)
});
function goToProfile(){
    window.location.href = '/users/profile/current';
}