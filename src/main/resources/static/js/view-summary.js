document.addEventListener('DOMContentLoaded', () => {
    const viewForm = document.getElementById('viewForm');
    const summaryContainer = document.getElementById('summaryContainer');
    const summaryTableBody = document.getElementById('summaryTableBody');

    const paymentListContainer = document.getElementById('paymentListContainer');
    const paymentTableBody = document.getElementById('paymentTableBody');

    viewForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const contractId = document.getElementById('viewContractId').value;
        const token = sessionStorage.getItem("jwt");

        if (!contractId || isNaN(contractId)) {
            alert("Введите корректный ID договора");
            return;
        }

        try {
            // Получение LoanSummaryDTO
            const summaryResp = await fetch(`/api/loans/${contractId}/summary`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!summaryResp.ok) {
                const errorText = await summaryResp.text();
                alert("Ошибка при получении сводной информации: " + errorText);
                return;
            }

            const summary = await summaryResp.json();
            renderSummary(summary);

            // Получение списка платежей
            const paymentResp = await fetch(`/api/schedules/loan/${contractId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!paymentResp.ok) {
                const errorText = await paymentResp.text();
                alert("Ошибка при получении списка платежей: " + errorText);
                return;
            }

            const payments = await paymentResp.json();
            renderPayments(payments);

        } catch (error) {
            alert("Ошибка: " + error.message);
        }
    });

    function renderSummary(summary) {
        summaryContainer.style.display = 'block';
        summaryTableBody.innerHTML = `
            <tr>
                <td>${summary.totalPrincipalPaid?.toFixed(2) ?? '0.00'}</td>
                <td>${summary.totalInterestPaid?.toFixed(2) ?? '0.00'}</td>
                <td>${summary.remainingPrincipal?.toFixed(2) ?? '0.00'}</td>
                <td>${summary.totalOverpayment?.toFixed(2) ?? '0.00'}</td>
            </tr>
        `;
    }

    function renderPayments(payments) {
        paymentListContainer.style.display = 'block';
        paymentTableBody.innerHTML = '';

        if (!payments.length) {
            paymentTableBody.innerHTML = '<tr><td colspan="5">Нет платежей</td></tr>';
            return;
        }

        for (const payment of payments) {
            const isPaid = String(payment.paid).toLowerCase() === 'true';

            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${payment.paymentDate}</td>
                <td>${payment.principalAmount.toFixed(2)}</td>
                <td>${payment.interestAmount.toFixed(2)}</td>
                <td>${payment.totalPayment.toFixed(2)}</td>
                <td>${isPaid ? 'Да' : 'Нет'}</td>
            `;
            paymentTableBody.appendChild(row);
        }
    }

});
