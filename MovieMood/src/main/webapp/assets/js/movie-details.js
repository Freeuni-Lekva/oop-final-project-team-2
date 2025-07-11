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

        // Replace the existing submit rating button event listener with this code:
        submitRatingBtn.addEventListener('click', () => {
            if (currentRating > 0) {
                // Get movie ID from the rating container
                const movieId = document.querySelector('.rating-container').getAttribute('data-movie-id');

                // Create form data
                const formData = new FormData();
                formData.append('movieId', movieId);
                formData.append('rating', currentRating);

                // Submit to RatingServlet
                fetch(window.contextPath + '/rating', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: 'movieId=' + movieId + '&rating=' + currentRating
                })
                    .then(response => {
                        console.log('Response status:', response.status);
                        console.log('Response headers:', response.headers.get('content-type'));

                        // Check if response is JSON
                        const contentType = response.headers.get('content-type');
                        if (contentType && contentType.includes('application/json')) {
                            return response.json();
                        } else {
                            // If not JSON, get text to see what was returned
                            return response.text().then(text => {
                                console.log('Non-JSON response:', text);
                                throw new Error('Server returned non-JSON response: ' + text.substring(0, 100));
                            });
                        }
                    })
                    .then(data => {
                        if (data.success) {
                            alert(`Thank you for rating this movie ${currentRating} stars!`);
                            // Optionally disable the rating form after successful submission
                            submitRatingBtn.disabled = true;
                            submitRatingBtn.textContent = 'Rating Submitted';
                        } else {
                            alert('Failed to submit rating: ' + (data.message || 'Unknown error'));
                        }
                    })
                    .catch(error => {
                        console.error('Error submitting rating:', error);
                        alert('Error submitting rating: ' + error.message);
                    });
            } else {
                alert('Please select a rating first!');
            }
        });
    }

    // Function to add review dynamically
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

    // Favorites button functionality
    const favBtn = document.getElementById('favoritesBtn');
    if (favBtn) {
        // Set initial favorited class based on current state
        console.log('Initial favorites state:', favBtn.getAttribute('data-in-favorites'));
        if (favBtn.getAttribute('data-in-favorites') === 'true') {
            favBtn.classList.add('favorited');
            // Force red color with inline styles to override everything
            favBtn.style.background = 'linear-gradient(135deg, #e74c3c, #c0392b)';
            favBtn.style.border = '2px solid #e74c3c';
            favBtn.style.color = 'white';
            favBtn.style.boxShadow = '0 6px 20px rgba(231, 76, 60, 0.4)';
            console.log('Applied red styling to favorites button');
        }

        favBtn.addEventListener('click', function() {
            const btn = this;
            const movieId = btn.getAttribute('data-movie-id');
            const isInFavorites = btn.getAttribute('data-in-favorites') === 'true';
            const action = isInFavorites ? 'remove' : 'add';

            fetch('/favorites/action', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: 'action=' + action + '&movieId=' + movieId
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Update button state
                        const newIsInFavorites = action === 'add';
                        btn.setAttribute('data-in-favorites', newIsInFavorites);
                        btn.textContent = newIsInFavorites ? '♥' : '♡';
                        btn.title = newIsInFavorites ? 'Remove from Favorites' : 'Add to Favorites';

                        // Add/remove favorited class for red styling
                        console.log('Setting favorites state to:', newIsInFavorites);
                        if (newIsInFavorites) {
                            btn.classList.add('favorited');
                            // Force red color with inline styles
                            btn.style.background = 'linear-gradient(135deg, #e74c3c, #c0392b)';
                            btn.style.border = '2px solid #e74c3c';
                            btn.style.color = 'white';
                            btn.style.boxShadow = '0 6px 20px rgba(231, 76, 60, 0.4)';
                            console.log('Applied red styling to button');
                        } else {
                            btn.classList.remove('favorited');
                            // Reset to default styling
                            btn.style.background = 'rgba(255, 255, 255, 0.05)';
                            btn.style.border = '2px solid rgba(255, 255, 255, 0.3)';
                            btn.style.color = 'rgba(255, 255, 255, 0.8)';
                            btn.style.boxShadow = 'none';
                            console.log('Reset styling to default');
                        }
                    }
                });
        });
    }

    // Watchlist button functionality
    const watchlistBtn = document.getElementById('watchlistBtn');
    if (watchlistBtn) {
        watchlistBtn.addEventListener('click', function() {
            const btn = this;
            const movieId = btn.getAttribute('data-movie-id');
            const isInWatchlist = btn.getAttribute('data-in-watchlist') === 'true';
            const action = isInWatchlist ? 'remove' : 'add';

            fetch('/watchlist/action', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: 'action=' + action + '&movieId=' + movieId
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Update button state
                        const newIsInWatchlist = action === 'add';
                        btn.setAttribute('data-in-watchlist', newIsInWatchlist);
                        btn.textContent = newIsInWatchlist ? '✓ In Watchlist' : '+ Add to Watchlist';
                    }
                });
        });
    }

    // Add to List button functionality
    const addToListBtn = document.getElementById('addToListBtn');
    const listDropdown = document.getElementById('listDropdown');

    if (addToListBtn && listDropdown) {
        // Toggle dropdown on button click
        addToListBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            const isVisible = listDropdown.style.display === 'block';

            if (isVisible) {
                listDropdown.style.display = 'none';
            } else {
                // Position the dropdown relative to the button
                const rect = addToListBtn.getBoundingClientRect();
                listDropdown.style.top = (rect.bottom + 5) + 'px';
                listDropdown.style.left = rect.left + 'px';
                listDropdown.style.display = 'block';
            }
        });

        // Handle list option clicks
        document.querySelectorAll('.list-option').forEach(option => {
            option.addEventListener('click', function(e) {
                e.stopPropagation();

                const listId = this.getAttribute('data-list-id');
                const isInList = this.getAttribute('data-in-list') === 'true';
                const movieId = addToListBtn.getAttribute('data-movie-id');
                const action = isInList ? 'remove' : 'add';

                fetch('/list/action', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: 'action=' + action + '&movieId=' + movieId + '&listId=' + listId
                })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            // Update the option state
                            const newIsInList = action === 'add';
                            this.setAttribute('data-in-list', newIsInList);

                            const statusSpan = this.querySelector('.list-status');
                            statusSpan.textContent = newIsInList ? '✓' : '+';

                            // Show success message
                            const listName = this.querySelector('.list-name').textContent;
                            console.log(data.message + ': ' + listName);
                        } else {
                            console.error('Failed:', data.message);
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                    });
            });
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', function() {
            listDropdown.style.display = 'none';
        });
    }
});