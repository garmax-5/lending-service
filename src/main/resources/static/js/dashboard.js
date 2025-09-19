function showTab(tabId) {
    const allTabs = document.querySelectorAll('.tab-content');
    allTabs.forEach(tab => tab.style.display = 'none');

    const tabToShow = document.getElementById(tabId);
    if (tabToShow) tabToShow.style.display = 'block';

    // Скрываем все сообщения об успехе и ошибках
    document.querySelectorAll('.success-message').forEach(el => el.style.display = 'none');
    document.querySelectorAll('.error-message').forEach(el => el.remove());

    // Скрываем все контейнеры с результатами таблиц
    const resultContainers = ['calcResultContainer', 'summaryContainer', 'paymentListContainer', 'rateHistoryContainer'];
    resultContainers.forEach(id => {
        const container = document.getElementById(id);
        if (container) container.style.display = 'none';
    });

    // Подсветка активной вкладки
    document.querySelectorAll('.tab-button').forEach(btn => btn.classList.remove('active'));
    const activeButton = Array.from(document.querySelectorAll('.tab-button'))
                             .find(btn => btn.getAttribute('onclick')?.includes(tabId));
    if (activeButton) activeButton.classList.add('active');
}
