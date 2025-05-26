
//gọi đến api để tìm kiếm tên truyện theo keyword, nếu có truyện thì sẽ showSearchResult() và truyền các truyện tìm được làm tham số.
document.querySelector(".search-story").addEventListener("input", async (e) => {
  const keyword = e.target.value;

  if (keyword.trim().length === 0) {
    hideSearchResult();
    return;
  }

  try {
    const response = await fetch(
      `/search-stories?keyword=${encodeURIComponent(keyword)}`
    );
    const stories = await response.json();

    if (stories.length > 0) {
      showSearchResult(stories);
    } else {
      showNoResult();
    }
  } catch (error) {
    console.error("Lỗi khi tìm kiếm:", error);
  }
});
// nhận dữ liệu và tạo ra các thẻ để chứa
function showSearchResult(stories) {
  const resultContainer = document.querySelector(".search-result");
  resultContainer.classList.remove("d-none");
  const listGroup = resultContainer.querySelector(".list-group");
  listGroup.classList.remove("d-none");
  listGroup.innerHTML = "";

  stories.forEach((story) => {
    // Tạo thẻ item
    const item = document.createElement("li");
    item.className = "list-group-itemC d-flex align-items-center";
    item.setAttribute("data-id", story.id);

    // Tạo thẻ img
    const img = document.createElement("img");
    img.src = story.srcA;
    img.alt = story.title;
    img.className = "me-2";

    // Tạo thẻ link
    const link = document.createElement("a");
    link.href = "#";
    link.textContent = story.title;
    link.className = "text-dark hover-title";

    link.addEventListener("click", async (e) => {
      e.preventDefault();
      console.log(`Truyện được chọn: ${story.title}`);

      try {
        const response = await fetch(`/story/${story.id}`);
        const data = await response.json();
        console.log("Dữ liệu chi tiết:", data);

        localStorage.setItem("data", JSON.stringify(data));
        window.location.href = "/story-details";
      } catch (error) {
        console.error("Lỗi khi lấy chi tiết truyện:", error);
      }
    });

    item.appendChild(img);
    item.appendChild(link);

    listGroup.appendChild(item);
  });
}

function showNoResult() {
  const resultContainer = document.querySelector(".search-result");
  resultContainer.classList.remove("d-none");
  const listGroup = resultContainer.querySelector(".list-group");
  listGroup.classList.remove("d-none");
  listGroup.innerHTML = "";
  const item = document.createElement("span");
  item.style.color = "black";
  item.textContent = "Không tìm thấy kết quả";

  listGroup.appendChild(item);
}

function hideSearchResult() {
  const resultContainer = document.querySelector(".search-result");
  resultContainer.classList.add("d-none"); // Ẩn container
}

// 2
document.addEventListener("DOMContentLoaded", async function () {
  try {
    // Lấy dữ liệu từ API
	let body = document.body;
	if (body.id != "home") return;
    const response = await fetch("/stories-hot");
    const data = await response.json();
    console.log("stories-hot: ", data);
    // Kiểm tra xem dữ liệu có hợp lệ không
    if (data && data.stories) {
      console.log("có data gửi lên");
      // Gọi hàm để hiển thị dữ liệu truyện
      fetchStories(data.stories);
    } else {
      console.error("Dữ liệu không hợp lệ hoặc không có truyện");
    }
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu từ API:", error);
  }
});

// Bắt sự kiện khi người dùng click vào một thể loại
document.querySelectorAll(".dropdown-item").forEach((item) => {
  item.addEventListener("click", async function (event) {
    
    event.preventDefault();

    const category = this.getAttribute("data-category"); // Lấy thể loại từ thuộc tính data-category

    console.log("category:", category);

    // Fetch dữ liệu truyện từ backend
    try {
      const response = await fetch(`/stories/${category}?page=${1}`);
      const data = await response.json();

      if (data) {
        localStorage.setItem("stories", JSON.stringify(data.stories));
        localStorage.setItem("idtl", JSON.stringify(data.idtl));
        localStorage.setItem("TheLoai", JSON.stringify(data.TheLoai));
        console.log(data);

        window.location.href = "/category";
      }
    } catch (error) {
      console.error("Error fetching stories:", error);
    }
  });
});

//3

// Hàm để lấy dữ liệu từ API và hiển thị danh sách truyện
function fetchStories(data) {
  console.log("response", data);
  try {
    const storyList = document.getElementById("story-list");
    if (!data) return;
    storyList.innerHTML = "";

    // Lặp qua danh sách truyện và tạo các thẻ HTML
    data.forEach((story) => {
      const storyItem = document.createElement("div");
      storyItem.classList.add("story-item");

      storyItem.innerHTML = `<a  class="d-block text-decoration-none story-link" data-id="${story.id}" onclick="sendStoryName(this);">
                                        <div class="story-item__image">
                                            <img src="${story.srcA}" alt="${story.title}" class="img-fluid" width="150" height="230" loading="lazy" id="story-image">
                                        </div>
                                        <h3 class="story-item__name text-one-row story-name">${
                                          story.title}</h3>
                                        <div class="list-badge">${story.status.split(",").map((status) => `<span class="story-item__badge badge text-bg-${status.trim().toLowerCase() === "full"? "success": status.trim().toLowerCase() === "hot"? "danger": "info"}">${status.trim()}</span>`).join("")}
                                        </div>
                                    </a>
                              `;

      storyList.appendChild(storyItem);
    });
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu:", error);
  }
}

document.querySelectorAll(".story-item-no-image").forEach((storyItem) => {
  storyItem.addEventListener("click", async function (event) {
    // Preventing the default behavior
    event.preventDefault();

    // Extracting the data attributes from the clicked element
    const storyId = storyItem.getAttribute("data-story-id");
    const chapterId = storyItem.getAttribute("data-chapter-id");

    console.log("storyId", storyId);
    try {
      const response = await fetch(`/story/${storyId}`);
      const data = await response.json();
      console.log("truyện đã được click:", data);

      localStorage.setItem("data", JSON.stringify(data));

      window.location.href = "/story-details";
    } catch (error) {
      console.error("Lỗi khi lấy dữ liệu truyện:", error);
    }
  });
});

//5
async function sendStoryName(element) {
  console.log("đã vào hàm sendStoryName!");

  // Lấy giá trị data-id từ thuộc tính của thẻ <a>
  const storyId = element.getAttribute("data-id");
  console.log("storyId", storyId);
  try {
    // Gửi yêu cầu đến API để lấy thông tin truyện theo ID
    const response = await fetch(`/story/${storyId}`);
    const data = await response.json();
    console.log("truyện đã được click:", data);
    localStorage.setItem("data", JSON.stringify(data));
    window.location.href = "/story-details";
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu truyện:", error);
  }
}
async function sendCategoryName(element) {
  let idtl = element.getAttribute("data-category");
  console.log("idtl:", idtl);
  try {
    const response = await fetch(`/stories/${idtl}`);
    const data = await response.json();
	if (data) {
	        localStorage.setItem("stories", JSON.stringify(data.stories));
	        localStorage.setItem("idtl", JSON.stringify(data.idtl));
	        localStorage.setItem("TheLoai", JSON.stringify(data.TheLoai));
	        console.log(data);

	        window.location.href = "/category";
	      }
    } catch (error) {
    console.error("Error fetching stories:", error);
  }
}

///

document.getElementById("search-in-nav-collaps").addEventListener("input", async (e) => {
  const keyword = e.target.value;

  if (keyword.trim().length === 0) {
    hideSearchResultNav();
    return;
  }

  try {
    const response = await fetch(
      `/search-stories?keyword=${encodeURIComponent(keyword)}`
    );
    const stories = await response.json();

    if (stories.length > 0) {
      showSearchResultNav(stories);
    } else {
      showNoResultNav();
    }
  } catch (error) {
    console.error("Lỗi khi tìm kiếm:", error);
  }
});
// nhận dữ liệu và tạo ra các thẻ để chứa
function showSearchResultNav(stories) {
  const resultContainer = document.getElementById("search-result-in-nav-collaps");
  resultContainer.classList.remove("d-none");
  const listGroup = resultContainer.querySelector(".list-group");
  listGroup.classList.remove("d-none");
  listGroup.innerHTML = "";

  stories.forEach((story) => {
    // Tạo thẻ item
    const item = document.createElement("li");
    item.className = "list-group-itemC d-flex align-items-center";
    item.setAttribute("data-id", story.id);

    // Tạo thẻ img
    const img = document.createElement("img");
    img.src = story.srcA;
    img.alt = story.title;
    img.className = "me-2";

    // Tạo thẻ link
    const link = document.createElement("a");
    link.href = "#";
    link.textContent = story.title;
    link.className = "text-dark hover-title";

    link.addEventListener("click", async (e) => {
      e.preventDefault();
      console.log(`Truyện được chọn: ${story.title}`);

      try {
        const response = await fetch(`/story/${story.id}`);
        const data = await response.json();
        console.log("Dữ liệu chi tiết:", data);

        localStorage.setItem("data", JSON.stringify(data));
        window.location.href = "/story-details";
      } catch (error) {
        console.error("Lỗi khi lấy chi tiết truyện:", error);
      }
    });

    item.appendChild(img);
    item.appendChild(link);

    // Gắn item vào danh sách
    listGroup.appendChild(item);
  });
}

function showNoResultNav() {
  const resultContainer = document.getElementById("search-result-in-nav-collaps");
  resultContainer.classList.remove("d-none");
  const listGroup = resultContainer.querySelector(".list-group");
  listGroup.classList.remove("d-none");
  listGroup.innerHTML = "";
  const item = document.createElement("span");
  item.style.color = "black";
  item.textContent = "Không tìm thấy kết quả";

  listGroup.appendChild(item);
}

function hideSearchResultNav() {
  const resultContainer = document.getElementById("search-result-in-nav-collaps");
  resultContainer.classList.add("d-none"); // Ẩn container
}


/// test
document.addEventListener("DOMContentLoaded", async function () {
  try {
    const response = await fetch("/stories-new");
    const data = await response.json();
	console.log("data truyen new:",data);
    if (data && data.stories) {
      showStoriesNew(data.stories);
    } else {
      console.error("khong co data");
    }
  } catch (error) {
    console.log("Error:", error);
  }
});

async function showStoriesNew(stories) {
  try {
    const story_new_list = document.querySelector(".section-stories-new__list");
    story_new_list.innerHTML = "";
    for (const story of stories) {
        const storyItem = document.createElement("div");
        storyItem.classList.add("story-item-no-image");
        storyItem.setAttribute("data-story-id", story.id);
        storyItem.innerHTML = `<div class="story-item-no-image__name d-flex align-items-center">
                                    <h3 class="me-1 mb-0 d-flex align-items-center">
                                        <svg style="width: 10px; margin-right: 5px;"
                                            xmlns="http://www.w3.org/2000/svg" height="1em"
                                            viewBox="0 0 320 512"><!--! Font Awesome Free 6.4.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license (Commercial License) Copyright 2023 Fonticons, Inc. -->
                                            <path
                                                d="M278.6 233.4c12.5 12.5 12.5 32.8 0 45.3l-160 160c-12.5 12.5-32.8 12.5-45.3 0s-12.5-32.8 0-45.3L210.7 256 73.4 118.6c-12.5-12.5-12.5-32.8 0-45.3s32.8-12.5 45.3 0l160 160z">
                                            </path>
                                        </svg>
                                        <a
                                            class="text-decoration-none text-dark fs-6 hover-title text-one-row story-name">${story.title}</a>
                                    </h3>
                                    <span class="badge text-bg-info text-light me-1">New</span>
                                    <span class="badge text-bg-success text-light me-1">Top Views</span>
                                    <span class="badge text-bg-danger text-light">Hot</span>
                                </div>`;
        const itemCategories = document.createElement("div");
        itemCategories.classList.add("story-item-no-image__categories","ms-2","d-none","d-lg-block");
        const itemCategory = document.createElement("p");
        itemCategory.classList.add("mb-0");
        try {
            const response = await fetch(`/category-of-story/${story.id}`);
            if (!response.ok) throw new Error(`Lỗi HTTP: ${response.status}`);
            const categories = await response.json();
			console.log("the loai cua truyen:",categories);
            categories.slice(0, 2).forEach((TL) => {
                const aElement = document.createElement("a");
                aElement.classList.add("hover-title", "text-decoration-none", "text-dark", "category-name");
                aElement.setAttribute("data-category", TL.id);
                aElement.textContent = TL.name+',';
                itemCategories.appendChild(aElement);
            });
        } catch (error) {
            console.error(`Lỗi lấy danh mục cho story ${story.id}:`, error);
        }
        storyItem.appendChild(itemCategories);
        const itemChapter = document.createElement("div");
        itemChapter.classList.add("story-item-no-image__chapters","ms-2")
        itemChapter.innerHTML =`<a class="hover-title text-decoration-none text-info"
                                                data-chapter-id="1">Chương 3</a>`
        storyItem.appendChild(itemChapter);
        story_new_list.appendChild(storyItem);
    };
  } catch (error) {}


document.querySelectorAll(".category-name").forEach((item) => {
    item.addEventListener("click", async function (event) {
      event.preventDefault();
  
      const category = this.getAttribute("data-category");
  
      console.log("category:", category);
  
      // Fetch dữ liệu truyện từ backend
      try {
        const response = await fetch(`/stories/${category}`);
        const data = await response.json();
  
        if (data) {
          localStorage.setItem("stories", JSON.stringify(data.stories));
          localStorage.setItem("idtl", JSON.stringify(data.idtl));
          localStorage.setItem("TheLoai", JSON.stringify(data.TheLoai));
          console.log(data);
  
          window.location.href = "/category";
        }
      } catch (error) {
        console.error("Error fetching stories:", error);
      }
    });
  });
  
  document.querySelectorAll(".story-item-no-image").forEach((element) => {
        element.addEventListener("click",async function (e) {
          e.preventDefault();
          const storyId = element.getAttribute("data-story-id");
          console.log("storyId", storyId);
          try {
            // Gửi yêu cầu đến API để lấy thông tin truyện theo ID
            const response = await fetch(`/story/${storyId}`);
            const data = await response.json();
            console.log("truyện đã được click:", data);
            localStorage.setItem("data", JSON.stringify(data));
            window.location.href = "/story-details";
          } catch (error) {
            console.error("Lỗi khi lấy dữ liệu truyện:", error);
          }
              })
          });
};


// page category

function updatePagination() {
    const paginationContainer = document.getElementById("pagination");
    paginationContainer.innerHTML = ""; // Xóa các nút cũ

    const createPageButton = (page) => {
        const btn = document.createElement("button");
        btn.textContent = page;
        if (page === currentPage) {
            btn.disabled = true;
            btn.style.fontWeight = "bold";
        }
        btn.addEventListener("click", () => loadPage(page));
        return btn;
    };

    // Thêm nút "Trang trước"
    if (currentPage > 1) {
        const prevBtn = document.createElement("button");
        prevBtn.textContent = "«";
        prevBtn.addEventListener("click", () => loadPage(currentPage - 1));
        paginationContainer.appendChild(prevBtn);
    }

    // Tính trang bắt đầu và kết thúc để hiển thị
    let startPage = Math.max(currentPage - 1, 1);
    let endPage = Math.min(startPage + 2, totalPages);
    if (endPage - startPage < 2) {
        startPage = Math.max(endPage - 2, 1);
    }

    // Nếu có trang đầu mà không hiển thị, thêm nút 1 và ...
    if (startPage > 1) {
        paginationContainer.appendChild(createPageButton(1));
        if (startPage > 2) {
            const dots = document.createElement("span");
            dots.textContent = "...";
            dots.style.margin = "0 5px";
            paginationContainer.appendChild(dots);
        }
    }

    // Các nút trang chính giữa
    for (let i = startPage; i <= endPage; i++) {
        paginationContainer.appendChild(createPageButton(i));
    }

    // Nếu còn trang cuối mà chưa hiển thị, thêm ... và trang cuối
    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            const dots = document.createElement("span");
            dots.textContent = "...";
            dots.style.margin = "0 5px";
            paginationContainer.appendChild(dots);
        }
        paginationContainer.appendChild(createPageButton(totalPages));
    }

    // Nút "Trang sau"
    if (currentPage < totalPages) {
        const nextBtn = document.createElement("button");
        nextBtn.textContent = "»";
        nextBtn.addEventListener("click", () => loadPage(currentPage + 1));
        paginationContainer.appendChild(nextBtn);
    }
}

currentPage = 1;
totalPages = 1;

async function loadPage(page) {
    currentPage = page;
	const categoryId = JSON.parse(localStorage.getItem('idtl'));
    const res = await fetch(`/stories/${categoryId}?page=${page}`);
    const storyData = await res.json();
    fetchStories(storyData.stories);
    updatePagination();
}
document.addEventListener('DOMContentLoaded', async function () {
    try {
        const categoryId = JSON.parse(localStorage.getItem('idtl'));
        const selectedCategory = JSON.parse(localStorage.getItem('TheLoai'));
        document.getElementById('selected-category').textContent = selectedCategory;

        const countResponse = await fetch(`/count-stories-of-category/${categoryId}`);
        const totalCount = await countResponse.json();
        const storiesPerPage = 1;
        totalPages = Math.ceil(totalCount / storiesPerPage); 

        currentPage = 1;

        
        // Gọi trang đầu tiên
        await loadPage(currentPage);

    } catch (error) {
        console.error("Lỗi:", error);
    }
});
//tmp
    
	