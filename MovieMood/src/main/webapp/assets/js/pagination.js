// Pagination functionality
let currentPage = 1;
let totalPages = 500;
const maxVisiblePages = 7;

// Initialize pagination on page load
document.addEventListener('DOMContentLoaded', function() {
    // Get current page from server-side attributes (JSP)
    const serverCurrentPage = parseInt(document.body.getAttribute('data-current-page') || '1');
    const serverTotalPages = parseInt(document.body.getAttribute('data-total-pages') || '500');

    currentPage = serverCurrentPage;
    totalPages = serverTotalPages;

    updatePagination();
});

function updatePagination() {
    const paginationNumbers = document.getElementById('paginationNumbers');
    const pageInfo = document.getElementById('pageInfo');
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');

    // Clear existing pagination numbers
    paginationNumbers.innerHTML = '';

    // Update page info
    pageInfo.textContent = `Page ${currentPage} of ${totalPages}`;

    // Update prev/next buttons
    prevBtn.disabled = currentPage === 1;
    nextBtn.disabled = currentPage === totalPages;

    // Calculate page numbers to show
    const pageNumbers = calculatePageNumbers();

    // Generate pagination numbers
    pageNumbers.forEach(pageNum => {
        const pageElement = document.createElement('button');
        pageElement.className = 'page-number';

        if (pageNum === '...') {
            pageElement.textContent = '...';
            pageElement.classList.add('ellipsis');
        } else {
            pageElement.textContent = pageNum;
            pageElement.onclick = () => goToPage(pageNum);

            if (pageNum === currentPage) {
                pageElement.classList.add('active');
            }
        }

        paginationNumbers.appendChild(pageElement);
    });
}

function calculatePageNumbers() {
    const pages = [];

    if (totalPages <= maxVisiblePages) {
        // Show all pages if total is less than max visible
        for (let i = 1; i <= totalPages; i++) {
            pages.push(i);
        }
    } else {
        // Always show first page
        pages.push(1);

        // Calculate start and end of middle section
        let start = Math.max(2, currentPage - 2);
        let end = Math.min(totalPages - 1, currentPage + 2);

        // Add ellipsis after first page if needed
        if (start > 2) {
            pages.push('...');
        }

        // Add middle pages
        for (let i = start; i <= end; i++) {
            pages.push(i);
        }

        // Add ellipsis before last page if needed
        if (end < totalPages - 1) {
            pages.push('...');
        }

        // Always show last page
        if (totalPages > 1) {
            pages.push(totalPages);
        }
    }

    return pages;
}

function goToPage(page) {
    if (page < 1 || page > totalPages || page === currentPage) {
        return;
    }

    // Redirect to the same URL with page parameter
    const url = new URL(window.location);
    url.searchParams.set('page', page);
    window.location.href = url.toString();
}

function changePage(direction) {
    const newPage = currentPage + direction;
    goToPage(newPage);
}

// Optional: Add keyboard navigation
document.addEventListener('keydown', function(e) {
    if (e.key === 'ArrowLeft') {
        changePage(-1);
    } else if (e.key === 'ArrowRight') {
        changePage(1);
    }
});

// Function to integrate with your existing filter system
function resetToFirstPage() {
    currentPage = 1;
    updatePagination();
}

// Call this function when filters change
function onFiltersChange() {
    resetToFirstPage();
    // Your existing filter logic here
}