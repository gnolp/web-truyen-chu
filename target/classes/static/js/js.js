

document.addEventListener("DOMContentLoaded", () => {
  const data = JSON.parse(localStorage.getItem("data"));

  if (data) {
    // Gọi hàm hiển thị dữ liệu
    displayStoryData(data);
    displayChapters(data);
  }
});
function displayStoryData(data) {
  const authorId = data.story.author_id;
  
  document.getElementById("story-title").textContent = data.story.title;
  document.getElementById("authorName").textContent = data["bút danh"];
  localStorage.setItem("authorId", authorId);
  document.getElementById(
    "authorName"
  ).href = `/account?userId=${authorId}`;
  document.getElementById("img-fluid-img").src = data.story.srcA;
  document.getElementById("text-info-status").textContent =
    data.story.status;
  document.getElementById("theloai").textContent = data.theloai;
  // Điền thông tin mô tả
  document.getElementById("story-review").textContent = data.story.mo_ta;

  localStorage.setItem("story-name", data.story.title);
  localStorage.setItem("story-id", data.story.id);
}
let currentPage = 1;
const chaptersPerPage = 25;

function displayChapters(data) {
  const totalChapters = data.Chuong;
  const chapterListDiv = document.getElementById("chapter-list");
  chapterListDiv.innerHTML = ""; // Xóa nội dung cũ

  // Tạo hai cột
  const column1 = document.createElement("div");
  const column2 = document.createElement("div");
  column1.classList.add("column");
  column2.classList.add("column");

  // Lấy chỉ số bắt đầu và kết thúc
  const startIndex = (currentPage - 1) * chaptersPerPage;
  const endIndex = Math.min(
    startIndex + chaptersPerPage,
    totalChapters.length
  );

  // Lấy các chương cần hiển thị
  const chaptersToDisplay = totalChapters.slice(startIndex, endIndex);

  chaptersToDisplay.forEach((chapter, index) => {
    const chapterDiv = document.createElement("div");
    chapterDiv.textContent = `Chương ${chapter.number}: ${chapter.title}`;
    chapterDiv.classList.add("chapter-item");
    chapterDiv.style.cursor = "pointer";

    // Thêm sự kiện click , lưu chương vào localStorage
    chapterDiv.addEventListener("click", function () {
      const chapterData = {
        number: chapter.number,
        title: chapter.title,
        content: chapter.content,
        id: chapter.id,
      };
	  const viewsLog={
		bookId:data.story.id,
		chapterId: chapter.id
	  }
	  fetch("/increase-views", {
	          method: "PATCH",
	          headers: {
	              "Content-Type": "application/json",
	          },
	          body: JSON.stringify(viewsLog)
	      }).catch(error => console.error("Lỗi cập nhật lượt xem:", error));
      localStorage.setItem(
        "selectedChapter",
        JSON.stringify(chapterData)
      );
      console.log("chapterdata:", chapterData);
      // Chuyển hướng sang trang chapter.html
      window.location.href = "/chapter.html";
    });

    // Phân chia chương vào 2 cột
    if (index < 24) {
      column1.appendChild(chapterDiv);
    } else {
      column2.appendChild(chapterDiv);
    }
  });

  // Thêm hai cột vào chapterListDiv
  chapterListDiv.appendChild(column1);
  chapterListDiv.appendChild(column2);

  // Cập nhật trạng thái của nút phân trang
  if (totalChapters.length > chaptersPerPage) {
    updatePagination();
  }
}

function updatePagination() {
  const prevButton = document.getElementById("prevPage");
  const nextButton = document.getElementById("nextPage");
  const pageIndicator = document.getElementById("pageIndicator");

  // Cập nhật chỉ thị trang
  pageIndicator.textContent = `Trang ${currentPage}`;

  // Kích hoạt hoặc vô hiệu hóa nút trước và tiếp theo
  prevButton.disabled = currentPage === 1;
  nextButton.disabled =
    currentPage * chaptersPerPage >= totalChapters.length;

  // Gán sự kiện cho nút trước và tiếp theo
  prevButton.onclick = () => {
    if (currentPage > 1) {
      currentPage--;
      displayChapters();
    }
  };

  nextButton.onclick = () => {
    if (currentPage * chaptersPerPage < totalChapters.length) {
      currentPage++;
      displayChapters();
    }
  };
}


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
        window.location.href = "/story-details.html";
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
      const response = await fetch(`/stories/${category}`);
      const data = await response.json();

      if (data) {
        localStorage.setItem("stories", JSON.stringify(data.stories));
        localStorage.setItem("idtl", JSON.stringify(data.idtl));
        localStorage.setItem("TheLoai", JSON.stringify(data.TheLoai));
        console.log(data);

        window.location.href = "/category.html";
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

      window.location.href = "/story-details.html";
    } catch (error) {
      console.error("Lỗi khi lấy dữ liệu truyện:", error);
    }
  });
});

//5
// Hàm để gửi tên truyện và chuyển hướng đến trang chi tiết truyện
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
    window.location.href = "/story-details.html";
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

	        window.location.href = "/category.html";
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
        window.location.href = "/story-details.html";
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
  
          window.location.href = "/category.html";
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
            window.location.href = "/story-details.html";
          } catch (error) {
            console.error("Lỗi khi lấy dữ liệu truyện:", error);
          }
              })
          });
};


// page category

document.addEventListener('DOMContentLoaded', async function () {
    try {
        const response = localStorage.getItem('stories');
        const nameTl = JSON.parse(localStorage.getItem('TheLoai'));
        console.log("truyện lấy được:", response);
        document.getElementById('selected-category').textContent = nameTl;
        if (response) {
            const data = JSON.parse(response);
            console.log("stories: ", data);

            if (data.length > 0) {
                console.log("Có data gửi lên");

				let currentPage = 1;
				                const storiesPerPage = 10; // Giới hạn 10 truyện mỗi trang
				                const totalPages = Math.ceil(data.length / storiesPerPage);

				                function displayStories(page) {
				                    const startIndex = (page - 1) * storiesPerPage;
				                    const endIndex = Math.min(startIndex + storiesPerPage, data.length);
				                    const storiesToDisplay = data.slice(startIndex, endIndex);

				                    fetchStories(storiesToDisplay); // Hiển thị danh sách truyện của trang hiện tại
				                    updatePagination();
				                }

				                function updatePagination() {
				                    const prevButton = document.getElementById("prevPage");
				                    const nextButton = document.getElementById("nextPage");
				                    const pageIndicator = document.getElementById("pageIndicator");

				                    pageIndicator.textContent = `Trang ${currentPage} / ${totalPages}`;

				                    prevButton.disabled = currentPage === 1;
				                    nextButton.disabled = currentPage === totalPages;

				                    prevButton.onclick = () => {
				                        if (currentPage > 1) {
				                            currentPage--;
				                            displayStories(currentPage);
				                        }
				                    };

				                    nextButton.onclick = () => {
				                        if (currentPage < totalPages) {
				                            currentPage++;
				                            displayStories(currentPage);
				                        }
				                    };
				                }

				                displayStories(currentPage); // Hiển thị trang đầu tiên
				            } else {
				                const storyList = document.getElementById("story-list");
				                const messageElement = document.createElement("div");
				                messageElement.textContent = "Không có truyện của thể loại này!";
				                storyList.appendChild(messageElement);
				            }
				        } else {
				            console.error("Không có dữ liệu trong localStorage");
				        }
				    } catch (error) {
				        console.error("Lỗi khi lấy dữ liệu từ localStorage:", error);
				    }
				});
//tmp
    
	