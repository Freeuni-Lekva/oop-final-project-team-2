
function updateSelection() {
    const allCheckboxes = document.querySelectorAll('.movie-checkbox');
    let count = 0;

    allCheckboxes.forEach(checkbox => {
        if (checkbox.checked) {
            count++;
        }
    });

    // Update counter
    const counterElement = document.getElementById('selectionCount');
    if (counterElement) {
        counterElement.textContent = 'Selected: ' + count + ' movies';
    }

    // Visual feedback
    document.querySelectorAll('.selectable-movie-card').forEach(card => {
        const checkbox = card.querySelector('.movie-checkbox');
        if (checkbox.checked) {
            card.classList.add('selected');
        } else {
            card.classList.remove('selected');
        }
    });

    // Button state
    const submitBtn = document.getElementById('submitBtn');
    if (count >= 3 && count <= 4) {
        submitBtn.disabled = false;
    } else {
        submitBtn.disabled = true;
    }
}

function showAlert(message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'error-alert';
    alertDiv.textContent = message;
    document.body.appendChild(alertDiv);

    setTimeout(() => {
        alertDiv.remove();
    }, 3000);
}

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.movie-checkbox').forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const selectedCount = document.querySelectorAll('.movie-checkbox:checked').length;

            if (selectedCount > 4) {
                this.checked = false;
                showAlert('You can only select up to 4 movies!');
                return;
            }

            updateSelection();
        });
    });

    // Initial update
    updateSelection();
});
