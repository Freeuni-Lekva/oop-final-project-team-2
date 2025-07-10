let currentSlide = 0;
let cardsPerView = 5;
let totalCards = 0;
let cardWidthWithGap = 0;

function initializeSlider() {
    const slider = document.getElementById('recommendedSlider');
    if (!slider) return;

    const cards = slider.querySelectorAll('.movie-card');
    totalCards = cards.length;

    if (totalCards > 0) {
        const firstCard = cards[0];
        const gap = 20; // must match CSS gap
        const cardWidth = firstCard.offsetWidth;
        cardWidthWithGap = cardWidth + gap;

        slider.style.width = `${cardWidthWithGap * totalCards}px`;

        updateCardsPerView();
        currentSlide = 0;
        slideMovies(0);
    }
}

function slideMovies(direction) {
    const slider = document.getElementById('recommendedSlider');
    if (!slider || totalCards === 0) return;

    const maxSlide = Math.max(0, totalCards - cardsPerView);

    currentSlide += direction;
    currentSlide = Math.max(0, Math.min(currentSlide, maxSlide));

    const shift = currentSlide * cardWidthWithGap;
    slider.style.transform = `translateX(-${shift}px)`;
}

function updateCardsPerView() {
    const width = window.innerWidth;
    let newCardsPerView = 5;

    if (width <= 480) newCardsPerView = 1;
    else if (width <= 768) newCardsPerView = 2;
    else if (width <= 1200) newCardsPerView = 4;

    if (newCardsPerView !== cardsPerView) {
        cardsPerView = newCardsPerView;
        currentSlide = 0;
        slideMovies(0);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    initializeSlider();
    window.addEventListener('resize', () => {
        initializeSlider();
        updateCardsPerView();
    });

    document.querySelectorAll('.filter-tag').forEach(tag => {
        tag.addEventListener('click', function () {
            this.classList.toggle('active');
        });
    });

    // const resetButton = document.querySelector('.reset-btn');
    // if (resetButton) {
    //     resetButton.addEventListener('click', function () {
    //         document.querySelectorAll('.filter-tag').forEach(tag => {
    //             tag.classList.remove('active');
    //         });
    //         document.querySelectorAll('.filter-select').forEach(select => {
    //             select.selectedIndex = 0;
    //         });
    //     });
    // }
});
