document.addEventListener('DOMContentLoaded', () => {
    const rateForm = document.getElementById('rateForm');
    const rateSuccess = document.getElementById('rateSuccess');
    const rateHistoryTableBody = document.getElementById('rateHistoryTableBody');
    const rateHistoryContainer = document.getElementById('rateHistoryContainer');
    const loadRateHistoryBtn = document.getElementById('loadRateHistory');

    const getToken = () => sessionStorage.getItem("jwt");

    rateForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        clearValidationErrors();
        rateHistoryContainer.style.display = 'none';
        rateHistoryTableBody.innerHTML = '';

        const contractIdInput = document.getElementById('rateContractId');
        const contractId = contractIdInput.value.trim();
        const newRate = document.getElementById('newRate').value.trim();
        const startDate = document.getElementById('rateStartDate').value.trim();

        const errors = [];

        if (!contractId || isNaN(contractId) || Number(contractId) <= 0) {
            errors.push({ field: 'rateContractId', message: 'Введите корректный ID договора' });
        }

        const rateValue = parseFloat(newRate);
        if (!newRate || isNaN(rateValue) || rateValue <= 0 || rateValue > 100) {
            errors.push({ field: 'newRate', message: 'Ставка должна быть числом от 0 до 100' });
        }

        if (!startDate) {
            errors.push({ field: 'rateStartDate', message: 'Укажите дату начала действия' });
        }

        if (errors.length > 0) {
            showValidationErrors(errors);
            if (errors[0]?.field) document.getElementById(errors[0].field).focus();
            return;
        }

        try {
            const response = await fetch(`/api/rates/loan/${contractId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${getToken()}`
                },
                body: JSON.stringify({ newRate: rateValue, startDate })
            });

            if (!response.ok) {
                const text = await response.text();
                throw new Error(text);
            }

            rateSuccess.style.display = 'block';
            rateForm.reset();
            rateHistoryContainer.style.display = 'none';
        } catch (err) {
            alert("Ошибка изменения ставки: " + err.message);
        }
    });

    loadRateHistoryBtn.addEventListener('click', async () => {
        clearValidationErrors();
        rateHistoryContainer.style.display = 'none';
        rateHistoryTableBody.innerHTML = '';

        const contractIdInput = document.getElementById('rateContractId');
        const contractId = contractIdInput.value.trim();

        if (!contractId || isNaN(contractId) || Number(contractId) <= 0) {
            showValidationErrors([{ field: 'rateContractId', message: 'Введите корректный ID договора' }]);
            contractIdInput.focus();
            return;
        }

        try {
            const response = await fetch(`/api/rates/loan/${contractId}`, {
                headers: { 'Authorization': `Bearer ${getToken()}` }
            });

            if (!response.ok) {
                const text = await response.text();
                throw new Error(text);
            }

            const history = await response.json();

            if (!history.length) {
                rateHistoryTableBody.innerHTML = `<tr><td colspan="3">История отсутствует</td></tr>`;
            } else {
                rateHistoryTableBody.innerHTML = history.map(entry => `
                    <tr>
                        <td>${entry.startDate}</td>
                        <td>${parseFloat(entry.rate).toFixed(2)}</td>
                        <td>${entry.employee?.fullName || '—'}</td>
                    </tr>
                `).join('');
            }

            rateHistoryContainer.style.display = 'block';
        } catch (err) {
            alert("Ошибка загрузки истории ставок: " + err.message);
        }
    });

    function showValidationErrors(errors) {
        errors.forEach(error => {
            const field = document.getElementById(error.field);
            if (!field) return;
            field.classList.add('invalid');

            const msg = document.createElement('div');
            msg.classList.add('error-message');
            msg.innerText = error.message;
            field.parentElement.appendChild(msg);
        });
    }

    function clearValidationErrors() {
        document.querySelectorAll('.invalid').forEach(el => el.classList.remove('invalid'));
        document.querySelectorAll('.error-message').forEach(el => el.remove());
    }
});
