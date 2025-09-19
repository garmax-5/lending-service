document.addEventListener('DOMContentLoaded', () => {
    const viewForm = document.getElementById('viewForm');
    const summaryContainer = document.getElementById('summaryContainer');
    const summaryTableBody = document.getElementById('summaryTableBody');
    const paymentListContainer = document.getElementById('paymentListContainer');
    const paymentTableBody = document.getElementById('paymentTableBody');

    viewForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const contractIdInput = document.getElementById('viewContractId');
        const contractId = contractIdInput.value.trim();
        const errorDiv = document.getElementById('viewContractIdError');
        const token = sessionStorage.getItem("jwt");

        // Очистка состояния
        contractIdInput.classList.remove('input-error');
        if (errorDiv) errorDiv.textContent = '';
        summaryContainer.style.display = 'none';
        paymentListContainer.style.display = 'none';
        summaryTableBody.innerHTML = '';
        paymentTableBody.innerHTML = '';

        if (!contractId || isNaN(contractId)) {
            contractIdInput.classList.add('input-error');
            if (errorDiv) errorDiv.textContent = "Введите корректный числовой ID договора";
            contractIdInput.focus();
            return;
        }

        try {
            const summaryResp = await fetch(`/api/loans/${contractId}/summary`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (!summaryResp.ok) {
                contractIdInput.classList.add('input-error');
                if (errorDiv) errorDiv.textContent = "Договор с таким ID не найден";
                contractIdInput.focus();
                return;
            }

            const summary = await summaryResp.json();
            renderSummary(summary);

            const paymentResp = await fetch(`/api/schedules/loan/${contractId}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (!paymentResp.ok) {
                contractIdInput.classList.add('input-error');
                if (errorDiv) errorDiv.textContent = "Ошибка при получении списка платежей";
                contractIdInput.focus();
                return;
            }

            const payments = await paymentResp.json();
            renderPayments(payments);

        } catch (error) {
            contractIdInput.classList.add('input-error');
            if (errorDiv) errorDiv.textContent = "Ошибка: " + error.message;
            contractIdInput.focus();
        }
    });

    function formatNumber(value) {
        return (value !== null && value !== undefined && !isNaN(value))
            ? Number(value).toFixed(2)
            : '0.00';
    }

    function renderSummary(summary) {
        summaryContainer.style.display = 'block';
        summaryTableBody.innerHTML = `
            <tr>
                <td>${formatNumber(summary.totalPrincipalPaid)}</td>
                <td>${formatNumber(summary.totalInterestPaid)}</td>
                <td>${formatNumber(summary.remainingPrincipal)}</td>
                <td>${formatNumber(summary.totalOverpayment)}</td>
            </tr>
        `;
    }

    function renderPayments(payments) {
        if (!payments.length) {
            paymentListContainer.style.display = 'block';
            paymentTableBody.innerHTML = '<tr><td colspan="5">Нет платежей</td></tr>';
            return;
        }

        paymentListContainer.style.display = 'block';
        paymentTableBody.innerHTML = '';

        payments.forEach(payment => {
            const isPaid = payment.isPaid === true; // поле из PaymentDTO
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${payment.paymentDate}</td>
                <td>${formatNumber(payment.principalAmount)}</td>
                <td>${formatNumber(payment.interestAmount)}</td>
                <td>${formatNumber(payment.totalPayment)}</td>
                <td>${isPaid ? 'Да' : 'Нет'}</td>
            `;
            paymentTableBody.appendChild(row);
        });
    }
});
