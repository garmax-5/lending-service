document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('loanForm');
    const successMessage = document.getElementById('successMessage');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        clearValidationErrors();

        const formData = new FormData(form);
        const payload = {
            clientFullName: formData.get('clientFullName').trim(),
            loanAmount: parseFloat(formData.get('loanAmount')),
            rate: parseFloat(formData.get('rate')),
            startDate: formData.get('startDate'),
            endDate: formData.get('endDate'),
            paymentType: formData.get('paymentType')
        };

        const validationErrors = validateForm(payload);
        if (validationErrors.length > 0) {
            showValidationErrors(validationErrors);
            return;
        }

        const token = sessionStorage.getItem("jwt");

        try {
            const response = await fetch('/api/loans', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                successMessage.style.display = 'block';
                form.reset();
            } else {
                const errorResponse = await response.json();
                const message = errorResponse.message || 'Неизвестная ошибка';
                // Подсветим clientFullName, если ошибка про клиента
                if (message.includes("Клиент")) {
                    showValidationErrors([{ field: 'clientFullName', message }]);
                } else {
                    alert('Ошибка при создании кредита:\n' + message);
                }
            }
        } catch (err) {
            alert('Сервер недоступен.');
        }
    });

    function validateForm(data) {
        const errors = [];

        if (!data.clientFullName || data.clientFullName.length < 5)
            errors.push({ field: 'clientFullName', message: 'Введите корректное ФИО клиента' });

        if (!data.loanAmount || data.loanAmount <= 0 || isNaN(data.loanAmount))
            errors.push({ field: 'loanAmount', message: 'Введите положительное значение суммы' });

        if (!data.rate || data.rate <= 0 || data.rate > 100 || isNaN(data.rate))
            errors.push({ field: 'rate', message: 'Процент должен быть от 0 до 100' });

        const minValidDate = new Date('1900-01-01');

        if (!data.startDate) {
            errors.push({ field: 'startDate', message: 'Укажите дату начала' });
        } else if (new Date(data.startDate) < minValidDate) {
            errors.push({ field: 'startDate', message: 'Дата начала слишком ранняя' });
        }

        if (!data.endDate) {
            errors.push({ field: 'endDate', message: 'Укажите дату окончания' });
        } else if (new Date(data.endDate) < minValidDate) {
            errors.push({ field: 'endDate', message: 'Дата окончания слишком ранняя' });
        }

        if (data.startDate && data.endDate && new Date(data.endDate) < new Date(data.startDate)) {
            errors.push({ field: 'endDate', message: 'Дата окончания должна быть позже начала' });
        }

        return errors;
    }

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
        document.querySelectorAll('.invalid').forEach(e => e.classList.remove('invalid'));
        document.querySelectorAll('.error-message').forEach(e => e.remove());
    }
});
