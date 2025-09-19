document.addEventListener('DOMContentLoaded', () => {
    const calcForm = document.getElementById('calcForm');
    const calcSuccess = document.getElementById('calcSuccess');
    const calcResultContainer = document.getElementById('calcResultContainer');
    const calcResultTableBody = document.getElementById('calcResultTableBody');

    const token = sessionStorage.getItem("jwt");

    if (calcForm) {
        calcForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const contractIdInput = document.getElementById('contractId');
            const contractId = Number(contractIdInput.value.trim());
            const errorDiv = document.getElementById('contractIdError');

            // Очистка предыдущих ошибок
            contractIdInput.classList.remove('input-error');
            errorDiv.textContent = '';
            calcResultContainer.style.display = 'none';
            calcResultTableBody.innerHTML = '';

            if (!contractId || isNaN(contractId)) {
                contractIdInput.classList.add('input-error');
                errorDiv.textContent = "Введите корректный числовой ID договора";
                return;
            }

            try {
                // Перерасчет графика
                const res = await fetch(`/api/loans/${contractId}/calculate-schedule`, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                });

                if (!res.ok) {
                    let message = "Произошла ошибка";
                    try {
                        const errorData = await res.json();
                        if (errorData.message === "Loan not found") {
                            message = "Договор с таким ID не найден";
                        } else {
                            message = errorData.message || message;
                        }
                    } catch (e) {
                        message = "Ошибка при расчете: " + res.statusText;
                    }
                    contractIdInput.classList.add('input-error');
                    errorDiv.textContent = message;
                    return;
                }

                // Показ сообщения об успехе
                calcSuccess.style.display = 'block';
                setTimeout(() => {
                    calcSuccess.style.display = 'none';
                    calcForm.reset();
                }, 3000);

                // Получение графика
                const paymentsRes = await fetch(`/api/schedules/loan/${contractId}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                });

                if (!paymentsRes.ok) {
                    const errorText = await paymentsRes.text();
                    contractIdInput.classList.add('input-error');
                    errorDiv.textContent = "Ошибка загрузки графика: " + errorText;
                    return;
                }

                const payments = await paymentsRes.json();
                renderPayments(payments);

            } catch (error) {
                contractIdInput.classList.add('input-error');
                errorDiv.textContent = "Ошибка: " + error.message;
            }
        });
    }

    function renderPayments(payments) {
        calcResultContainer.style.display = 'block';
        calcResultTableBody.innerHTML = '';

        if (!payments.length) {
            calcResultTableBody.innerHTML = '<tr><td colspan="5">Нет платежей</td></tr>';
            return;
        }

        for (const p of payments) {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${p.paymentDate}</td>
                <td>${p.principalAmount.toFixed(2)}</td>
                <td>${p.interestAmount.toFixed(2)}</td>
                <td>${p.totalPayment.toFixed(2)}</td>
                <td>${p.isPaid ? 'Да' : 'Нет'}</td>
            `;
            calcResultTableBody.appendChild(row);
        }
    }
});
