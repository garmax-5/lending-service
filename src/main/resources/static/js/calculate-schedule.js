document.addEventListener('DOMContentLoaded', () => {
    const calcForm = document.getElementById('calcForm');
    const calcSuccess = document.getElementById('calcSuccess');
    const calcResultContainer = document.getElementById('calcResultContainer');
    const calcResultTableBody = document.getElementById('calcResultTableBody');

    const token = sessionStorage.getItem("jwt");

    if (calcForm) {
        calcForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const contractId = document.getElementById('contractId').value;

            try {
                // 1. Перерасчет графика
                const res = await fetch(`/api/loans/${contractId}/calculate-schedule`, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (!res.ok) {
                    const error = await res.text();
                    throw new Error("Ошибка при расчете: " + error);
                }

                calcSuccess.style.display = 'block';

                // 2. Получение обновленного графика
                const paymentsRes = await fetch(`/api/schedules/loan/${contractId}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (!paymentsRes.ok) {
                    const errorText = await paymentsRes.text();
                    throw new Error("Ошибка загрузки графика: " + errorText);
                }

                const payments = await paymentsRes.json();
                renderPayments(payments);

            } catch (error) {
                alert("Ошибка: " + error.message);
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
                <td>${p.paid ? 'Да' : 'Нет'}</td>
            `;
            calcResultTableBody.appendChild(row);
        }
    }
});
