document.addEventListener('DOMContentLoaded', function () {
    // Rating System - Only initialize if elements exist
    const stars = document.querySelectorAll('.star');
    const ratingText = document.getElementById('ratingText');
    const submitRatingBtn = document.getElementById('submitRating');
    const ratingStars = document.getElementById('ratingStars');

    if (stars.length > 0 && ratingText && submitRatingBtn && ratingStars) {
        let currentRating = 0;

        stars.forEach((star, index) => {
            star.addEventListener('click', () => {
                currentRating = index + 1;
                updateStars();
                updateRatingText();
            });

            star.addEventListener('mouseenter', () => {
                highlightStars(index + 1);
            });
        });

        ratingStars.addEventListener('mouseleave', () => {
            updateStars();
        });

        function updateStars() {
            stars.forEach((star, index) => {
                star.classList.toggle('active', index < currentRating);
            });
        }

        function highlightStars(rating) {
            stars.forEach((star, index) => {
                star.classList.toggle('active', index < rating);
            });
        }

        function updateRatingText() {
            const ratingTexts = ['', 'Poor', 'Fair', 'Good', 'Very Good', 'Excellent'];
            ratingText.textContent = ratingTexts[currentRating] || 'Click a star to rate';
        }

        submitRatingBtn.addEventListener('click', () => {
            if (currentRating > 0) {
                alert(`Thank you for rating this movie ${currentRating} stars!`);
                // Send to server logic goes here
            } else {
                alert('Please select a rating first!');
            }
        });
    }

    // Smooth scrolling for internal links - Only if links exist
    const internalLinks = document.querySelectorAll('a[href^="#"]');
    if (internalLinks.length > 0) {
        internalLinks.forEach(anchor => {
            anchor.addEventListener('click', function (e) {
                e.preventDefault();
                const targetElement = document.querySelector(this.getAttribute('href'));
                if (targetElement) {
                    targetElement.scrollIntoView({
                        behavior: 'smooth'
                    });
                }
            });
        });
    }
});