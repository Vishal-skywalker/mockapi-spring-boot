const form = document.getElementById('registerForm');
form.addEventListener('submit', async function (event) {
    event.preventDefault(); // Prevent default form submission

    const validationErrors = document.getElementById('validationErrors');
    validationErrors.classList.add('d-none'); // Hide error message area

    if (!form.checkValidity()) {
        event.preventDefault(); // Stop form submission
        event.stopPropagation(); // Stop further event handling
        form.classList.add('was-validated'); // Add Bootstrap validation styling
        return;
    }

    // Collect form data
    const formData = {
        name: document.getElementById('name').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
    };

    try {
        // Send POST request to /validateUser
        const response = await fetch('/validateUser', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData),
        });

        const result = await response.json();

        if (result.isError) {
            // Display validation errors
            validationErrors.textContent = result.errorMessage || 'Validation failed.';
            validationErrors.classList.remove('d-none');
        } else {
            // Submit the form to /register
            const registerForm = document.getElementById('registerForm');
            registerForm.method = 'POST';
            registerForm.action = '/register' + window.location.search.toString();
            registerForm.submit();
        }
    } catch (error) {
        // Handle unexpected errors
        validationErrors.textContent = 'An error occurred during validation.';
        validationErrors.classList.remove('d-none');
    }
});