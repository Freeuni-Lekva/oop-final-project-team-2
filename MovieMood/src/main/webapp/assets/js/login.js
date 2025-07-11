/**
 * Display error message to user
 */
function showError(message) {
    alert(message);
}

/**
 * Google Sign-In JavaScript for login page
 */

function handleCredentialResponse(response) {
    fetch('/auth/google', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            credential: response.credential
        })
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                window.location.href = data.redirect;
            } else {
                alert('Google Sign-In failed: ' + (data.error || 'Unknown error'));
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Google Sign-In failed. Please try again.');
        });
}