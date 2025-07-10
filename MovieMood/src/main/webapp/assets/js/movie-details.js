document.addEventListener('DOMContentLoaded', function () {
    // Rating System
    let currentRating = 0;
    const stars = document.querySelectorAll('.star');
    const ratingText = document.getElementById('ratingText');
    const submitRatingBtn = document.getElementById('submitRating');

    if (stars.length > 0 && ratingText && submitRatingBtn) {
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

        document.getElementById('ratingStars').addEventListener('mouseleave', () => {
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

    // Review Form
    const reviewForm = document.getElementById('reviewForm');
    if (reviewForm) {
        reviewForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const name = "Anonymous"; // Or fetch dynamically
            const review = document.getElementById('reviewText').value;

            if (name && review) {
                addReview(name, review);
                reviewForm.reset();
            }
        });
    }

    function addReview(name, reviewText) {
        const reviewsList = document.getElementById('reviewsList');
        if (!reviewsList) return;

        const reviewItem = document.createElement('div');
        reviewItem.className = 'review-item';

        const initials = name.split(' ').map(n => n[0]).join('').toUpperCase();
        const currentDate = new Date().toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });

        reviewItem.innerHTML = `
            <div class="review-header">
                <div class="reviewer-info">
                    <div class="reviewer-avatar">${initials}</div>
                    <div>
                        <div class="reviewer-name">${name}</div>
                        <div class="review-date">${currentDate}</div>
                    </div>
                </div>
                <div class="review-rating">★★★★☆</div>
            </div>
            <div class="review-text">${reviewText}</div>
        `;

        reviewsList.insertBefore(reviewItem, reviewsList.firstChild);

        // Add animation
        reviewItem.style.opacity = '0';
        reviewItem.style.transform = 'translateY(20px)';
        setTimeout(() => {
            reviewItem.style.transition = 'all 0.3s ease';
            reviewItem.style.opacity = '1';
            reviewItem.style.transform = 'translateY(0)';
        }, 100);
    }

    // Smooth scrolling for internal links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            document.querySelector(this.getAttribute('href')).scrollIntoView({
                behavior: 'smooth'
            });
        });
    });
});
