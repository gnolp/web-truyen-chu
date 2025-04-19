     
        document.getElementById("confirmPassword").addEventListener("input", function () {
            let newPassword = document.getElementById("newPassword").value;
            let confirmPassword = this.value;
            let errorText = document.getElementById("passwordError");
        
            if (newPassword !== confirmPassword) {
                this.classList.add("is-invalid");
                errorText.classList.remove("d-none");
            } else {
                this.classList.remove("is-invalid");
                errorText.classList.add("d-none");
            }
        });
        document.getElementById("avatar-input").addEventListener("change", function(event) {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    document.getElementById("avatar-preview").src = e.target.result;
                };
                reader.readAsDataURL(file);
            }
        });
        document.querySelectorAll('.nav-tabs.T .nav-link[data-bs-toggle="tab"]').forEach(tab => {
            tab.addEventListener('click', (e) => {
                let link = tab.getAttribute("href");
                console.log('link',link);
                document.querySelectorAll('.nav-tabs .nav-link[data-bs-toggle="tab"]').forEach(a => {
                    a.classList.remove('active');
                });
                document.querySelectorAll(`[href="${link}"]`).forEach(a => {
                    a.classList.add('active');
                });
        
                document.querySelectorAll('.tab-pane').forEach(pane => {
                    pane.classList.remove("show", "active");
                });
                let targetPane = document.querySelector(link);
                if (targetPane) {
                    targetPane.classList.add("show", "active");
                }
            });
        });
        document.getElementById('sidebarMenu').addEventListener('shown.bs.offcanvas', () => {
            let activeTab = document.querySelector('.nav-tabs .nav-link.active');
            if (activeTab) {
                let link = activeTab.getAttribute("href");
                    document.querySelectorAll(`[href="${link}"]`).forEach(a => {
                    a.classList.add('active');
                });
        
                let targetPane = document.getElementById(link);
                if (targetPane) {
                    targetPane.classList.add("show", "active");
                }
            }
        });
		
// load data

function showNum(element, values) {
    element.setAttribute("data-value", values);
    element.textContent = values;
}

function showAuth(list, authors) {
    list.innerHTML = ``;
    for (let au of authors) {
        let tr = document.createElement("tr");
        tr.setAttribute("data-bs-toggle", "modal");
        tr.setAttribute("data-bs-target", "#Show-author");

        tr.innerHTML = `
            <td id="id_authors">${au.id}</td>
            <td><img src="${au.srcA}" style="width: 65px;"></td>
            <td>${au.name}</td>
            <td>${au.butdanh}</td>
            <td>${au.created_at}</td>
            <td>${au.so_truyen}</td>
            <td>${au.so_chuong}</td>
        `;

        tr.addEventListener("click", async () => {
            try {
                let response = await fetch(`/get-data-author?author_id=${au.id}`);
                if (!response.ok) throw new Error("Lỗi khi lấy dữ liệu");

                let data = await response.json();
				console.log("data author:",data);
                fillModalData(data);
            } catch (error) {
                console.error("Lỗi lấy dữ liệu:", error);
            }
        });

        list.appendChild(tr);
    }
}

function fillModalData(data) {
  document.querySelector("#profileA input[type='text']").value = data.first_name || "";
  document.querySelectorAll("#profileA input[type='text']")[1].value = data.last_name || "";
  document.querySelectorAll("#profileA input[type='text']")[2].value = data.username || "";
  document.querySelector("#profileA input[type='email']").value = data.email || "";
  
  document.querySelector("#avatar-previewA").src = data.srcA || "/images/default-avatar.jpg";
  document.getElementById("avatar-inputA").addEventListener("change", function(event) {
              const file = event.target.files[0];
              if (file) {
                  const reader = new FileReader();
                  reader.onload = function(e) {
                      document.getElementById("avatar-previewA").src = e.target.result;
                  };
                  reader.readAsDataURL(file);
              }
          });
  let storyList = document.querySelector("#story-of-A ul");
  storyList.innerHTML = (data.books || []).map(story => `
      <li class="nav-item pt-2 pb-2">
          <a href="#" class="text-decoration-none story-link" data-book-id="${story.book_id}">
              <img src="${story.image}" style="width:40px;height:40px">
              <span class="ms-5">${story.title}</span>
          </a>
      </li>
  `).join("");

  document.querySelectorAll(".story-link").forEach(link => {
      link.addEventListener("click", async function (event) {
          event.preventDefault();

          const bookId = this.getAttribute("data-book-id");

          try {
              const response = await fetch(`/story/${bookId}`);
              if (!response.ok) throw new Error("Lỗi khi lấy dữ liệu");

              const data = await response.json();
              console.log("Dữ liệu chi tiết:", data);

              localStorage.setItem("data", JSON.stringify(data));
              window.open("/story-details", "_blank");
          } catch (error) {
              console.error("Lỗi khi lấy chi tiết truyện:", error);
          }
      });
  });
  const updateButton = document.getElementById("update-author");
  updateButton.onclick = async function (event) {
      event.preventDefault(); // Ngăn chặn reload trang

      let formData = new FormData();
      formData.append("id", data.id);

      let avatarInput = document.getElementById("avatar-inputA");
      if (avatarInput.files.length > 0) {
          formData.append("avatar", avatarInput.files[0]);
      }
      formData.append("firstName", document.querySelector("#profileA input[type='text']").value);
      formData.append("lastName", document.querySelectorAll("#profileA input[type='text']")[1].value);
      formData.append("email", document.querySelector("#profileA input[type='email']").value);

      try {
          let response = await fetch("/update", {
              method: "POST",
              body: formData
          });

          console.log("Response status:", response.status);
          let result = await response.text();
          console.log("Phản hồi từ server:", result);

          if (response.ok) {
              alert("Cập nhật thành công!");
          } else {
              alert("Cập nhật thất bại: " + result);
          }
      } catch (error) {
          console.error("Lỗi khi gửi yêu cầu:", error);
      }
  };
  document.getElementById("updatePassWord").addEventListener("click", function () {
      const newPassword = document.getElementById("newPasswordA").value;
      const confirmPassword = document.getElementById("confirmPasswordA").value;
      const passwordError = document.getElementById("passwordErrorA");
      
      // Kiểm tra mật khẩu nhập lại có khớp không
      if (newPassword !== confirmPassword) {
          passwordError.classList.remove("d-none");
          return;
      } else {
          passwordError.classList.add("d-none");
      }
      
      // Gửi yêu cầu API
	  fetch(`/change-password?id=${data.id}`, {
	      method: "POST",
	      headers: {
	          "Content-Type": "application/json"
	      },
	      body: JSON.stringify({
	          newPassword: newPassword
	      })
	  })
	  .then(response => response.json())  // Chuyển response thành JSON
	  .then(data => {
	      if (data.success) {  // Kiểm tra key "success"
	          alert(data.message);  // Hiển thị thông báo từ server
	      } else {
	          alert("Error: " + data.message);
	      }
	  })
	  .catch(error => {
	      console.error("Error:", error);
	      alert("An error occurred. Please try again later.");
	  });

  });



  let modal = new bootstrap.Modal(document.getElementById("Show-author"));
  modal.show();
}


document.addEventListener("DOMContentLoaded", async function () {
    try {
        fetch("/admin-home")
            .then(response => response.json()) 
            .then(data => {
                let views = document.getElementById("views-of-the-day");
                let books = document.getElementById("new-books");
                let users = document.getElementById("users");
                let reports = document.getElementById("reports");
                let autherList = document.getElementById("tbodyA");
				console.log("data:",data);
                showNum(views, data.views_of_today);
                showNum(books, data.new_books);
                showNum(users, data.total_users);
                showNum(reports, data.report);
                showAuth(autherList, data.top3_authors);
            })
            .catch(error => console.error("Lỗi lấy dữ liệu:", error));
    } catch (error) {
        console.error("Lỗi:", error);
    }
});

function switchToUserTab() {
       let userTab = document.querySelector('#user-tab');
       if (userTab) {
           let bsTab = new bootstrap.Tab(userTab);
           bsTab.show();
       } else {
           console.error("Không tìm thấy tab với ID #user-tab");
       }
   }
// tab user:
document.addEventListener("DOMContentLoaded", function () {
    function fetchUsers(users) {
        console.log("users:", users);
        const tbody = document.getElementById("tbodyU");
        tbody.innerHTML = "";

        users.forEach(user => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td><input type="checkbox" class="user-checkbox"></td>
                <td>${user.id}</td>
                <td><img src="${user.scrA}" style="width: 65px; height:65px"></td>
                <td>${user.firstName + " " + user.lastName}</td>
                <td>${user.butdanh}</td>
                <td>${user.phonenumber}</td>
                <td>${user.email}</td>
                <td>${user.created_at}</td>
                <td>${user.namsinh}</td>
            `;
			tr.querySelector(".user-checkbox").addEventListener("click", function (event) {
			                event.stopPropagation();
			            });
            tr.addEventListener("click", async () => {
                try {
                    let response = await fetch(`/get-data-author?author_id=${user.id}`);
                    if (!response.ok) throw new Error("Lỗi khi lấy dữ liệu");

                    let data = await response.json();
                    console.log("data author:", data);
                    fillModalData(data);
                } catch (error) {
                    console.error("Lỗi lấy dữ liệu:", error);
                }
            });

            tbody.appendChild(tr);
        });
    }
	function fetchStory(stories) {
	    const tbody = document.getElementById("TbodyS");
	    tbody.innerHTML = ""; // Xóa nội dung cũ

	    stories.forEach(story => {
	        const row = document.createElement("tr");
			row.style.cursor = "pointer"; // Biến hàng thành có thể click
			row.dataset.id = story.id;
	        row.innerHTML = `
	            <td><input type="checkbox"></td>
	            <td style="text-align: center !important">${story.id}</td>
	            <td style="text-align: center !important">
	                <img src="${story.srcA}" alt="Story Image" width="50">
	            </td>
	            <td style="text-align: center !important">${story.title}</td>
	            <td style="text-align: center !important">${story.mo_ta}</td>
	            <td style="text-align: center !important">${story.status}</td>
	            <td style="text-align: center !important">${story.created_at}</td>
	            <td style="text-align: center !important">${story.luotdoc}</td>
	            <td style="text-align: center !important">${story.so_chuong}</td>
	        `;
			row.addEventListener("click", async function (event) {
			            
			            if (event.target.tagName === "INPUT") return;

			            const bookId = this.dataset.id;

			            try {
			                const response = await fetch(`/story/${bookId}`);
			                if (!response.ok) throw new Error("Lỗi khi lấy dữ liệu");

			                const data = await response.json();
			                console.log("Dữ liệu chi tiết:", data);

			                localStorage.setItem("data", JSON.stringify(data));
			                window.open("/story-details", "_blank");
			            } catch (error) {
			                console.error("Lỗi khi lấy chi tiết truyện:", error);
			            }
			        });
	        tbody.appendChild(row);
	    });
	}
    // Gán sự kiện khi nhấn vào tab "User"
    document.querySelectorAll('a[href="#User"]').forEach(element => {
        element.addEventListener("click", function () {
            console.log("Vào đến href này");
            fetch("/get-users")
                .then(response => response.json())
                .then(users => fetchUsers(users))
                .catch(error => console.error("Lỗi khi lấy danh sách users:", error));
        });
    });
	document.querySelectorAll('a[href="#Story"]').forEach(element => {
	        element.addEventListener("click", function () {
	            console.log("Vào đến href này");
	            fetch("/get-books")
	                .then(response => response.json())
	                .then(stories => fetchStory(stories))
	                .catch(error => console.error("Lỗi khi lấy danh sách story:", error));
	        });
	    });
	document.querySelector("#User .btn-delete").addEventListener("click", function () {
	        const tbody = document.querySelector("#User #tbodyU"); 
	        const checkboxes = tbody.querySelectorAll("input[type='checkbox']:checked"); // Lấy checkbox đã tích
	        
	        if (checkboxes.length === 0) {
	            alert("Vui lòng chọn ít nhất một người dùng để xóa.");
	            return;
	        }

	        let userIds = [];
	        checkboxes.forEach(checkbox => {
	            let row = checkbox.closest("tr");  // Lấy hàng chứa checkbox
	            let userId = row.children[1].textContent; // Cột chứa user.id
	            userIds.push(userId);
	        });

	        // Hiển thị xác nhận xóa
	        let confirmDelete = confirm(`Bạn có chắc chắn muốn xóa ${userIds.length} người dùng?`);
			if (!confirmDelete) return;

			        // Gửi request DELETE cho từng user
			        userIds.forEach(id => {
			            fetch(`/deleteUser/${id}`, {
			                method: "DELETE"
			            })
			            .then(response => {
			                if (!response.ok) throw new Error("Xóa thất bại");
			                return response.text();
			            })
			            .then(message => {
			                console.log(message);
			                // Xóa hàng đã xóa khỏi bảng
			                checkboxes.forEach(checkbox => checkbox.closest("tr").remove());
			            })
			            .catch(error => console.error("Lỗi:", error));
			        });
	    });
		document.querySelector("#Story .btn-delete").addEventListener("click", function () { 
		    const tbody = document.querySelector("#Story #TbodyS");  
		    const checkboxes = tbody.querySelectorAll("input[type='checkbox']:checked"); // Lấy checkbox đã tích
		    
		    if (checkboxes.length === 0) {
		        alert("Vui lòng chọn ít nhất một truyện để xóa.");
		        return;
		    }

		    let storyIds = [];
		    checkboxes.forEach(checkbox => {
		        let row = checkbox.closest("tr");
		        let storyId = row.children[1].textContent;
		        storyIds.push(storyId);
		    });

		    
		    let confirmDelete = confirm(`Bạn có chắc chắn muốn xóa ${storyIds.length} truyện?`);
		    if (!confirmDelete) return;

		    Promise.all(storyIds.map(id => 
		        fetch(`/delete-story/${id}`, { method: "DELETE" })
		        .then(response => {
		            if (!response.ok) throw new Error("Xóa thất bại");
		            return response.text();
		        })
		    ))
		    .then(() => {
		        console.log("Xóa thành công");
		        checkboxes.forEach(checkbox => checkbox.closest("tr").remove());
		    })
		    .catch(error => console.error("Lỗi khi xóa:", error));
		});

		const nameInput = document.querySelector('.table-search-input.U input[placeholder="Name..."]');
		const emailInput = document.querySelector('.table-search-input.U input[placeholder="Email..."]');
		const titleInput = document.querySelector('.table-search-input.S input[placeholder="Title..."]');
		const authorInput = document.querySelector('.table-search-input.S input[placeholder="Author..."]');
		function searchStories() {
		    const title = titleInput.value.trim();
			const author = authorInput.value.trim();
			console.log("author:",author);
		    let queryParams = [];
		    if (title) queryParams.push(`title=${encodeURIComponent(title)}`);
		    if (author) queryParams.push(`author=${encodeURIComponent(author)}`);
		    let queryString = queryParams.length ? `?${queryParams.join("&")}` : "";
			console.log("query",queryString);
		    fetch(`/admin/search-story${queryString}`)
		        .then(response => response.json())
		        .then(stories => {
		            console.log("Kết quả tìm kiếm:", stories);
		            fetchStory(stories);
		        })
		        .catch(error => console.error("Lỗi khi tìm kiếm story:", error));
		}
		
		    function searchUsers() {
		        const name = nameInput.value.trim();
		        const email = emailInput.value.trim();
		        
		        let queryParams = [];
		        if (name) queryParams.push(`name=${encodeURIComponent(name)}`);
		        if (email) queryParams.push(`email=${encodeURIComponent(email)}`);
		        let queryString = queryParams.length ? `?${queryParams.join("&")}` : "";

		        fetch(`admin/search-users${queryString}`)
		            .then(response => response.json())
		            .then(users => {
		                console.log("Kết quả tìm kiếm:", users);
		                fetchUsers(users);  // Gọi lại hàm hiển thị dữ liệu
		            })
		            .catch(error => console.error("Lỗi khi tìm kiếm:", error));
		    }

		    // Gán sự kiện khi nhập vào ô tìm kiếm
		    nameInput.addEventListener("input", searchUsers);
		    emailInput.addEventListener("input", searchUsers);
			titleInput.addEventListener("input", searchStories);
			authorInput.addEventListener("input", searchStories);
			
	/////
	document.querySelector('[data-bs-target="#New-stories"]')?.addEventListener('click', async () => {
	    try {
	        const response = await fetch('admin/get-truyen-new');
	        if (!response.ok) {
	            throw new Error('Lỗi khi lấy dữ liệu');
	        }
	        const data = await response.json();
	        
	        const tbody = document.getElementById('stories-new');
	        tbody.innerHTML = '';
	        
	        data.forEach(story => {
	            const row = document.createElement('tr');
	            row.innerHTML = `
	                <td style="text-align: center;">${story.id}</td>
	                <td style="text-align: center;"><img src="${story.srcA}" alt="Image" style="width: 80px; height: auto;"></td>
	                <td style="text-align: center;">${story.title}</td>
	                <td style="text-align: center;">${story.mo_ta}</td>
	                <td style="text-align: center;">${story.created_at}</td>
	            `;
				row.dataset.id = story.id;
				row.addEventListener("click", async function (event) {
					const bookId = this.dataset.id;
					console.log("boolId:",bookId);
					try {
						const response = await fetch(`/story/${bookId}`);
						if (!response.ok) throw new Error("Lỗi khi lấy dữ liệu");

						const data = await response.json();
						console.log("Dữ liệu chi tiết:", data);

						localStorage.setItem("data", JSON.stringify(data));
						window.open("/story-details", "_blank");
					} catch (error) {
						console.error("Lỗi khi lấy chi tiết truyện:", error);
					}
				});
	            tbody.appendChild(row);
	        });
	    } catch (error) {
	        console.error('Lỗi:', error);
	    }
	});
   ///report
   document.querySelectorAll('[data-bs-target="#Show-Report"]').forEach(button => {
       button.addEventListener("click", async function () {
           try {
               // Gọi API lấy dữ liệu report
               let response = await fetch("/admin/get-reports");
               let reports = await response.json(); // Chuyển dữ liệu thành JSON

               // Kiểm tra nếu có dữ liệu hợp lệ
               if (!Array.isArray(reports)) {
                   console.error("Dữ liệu không hợp lệ:", reports);
                   return;
               }
               console.log("📌 Reports:", reports);

               let container = document.querySelector("#Show-Report .modal-body .container");
               container.innerHTML = ""; // Xóa nội dung cũ

               reports.forEach(report => {
                   let rowElement = document.createElement("div");
                   rowElement.className = "row pb-2 pt-2";
                   rowElement.style.borderBottom = "1px solid #b6a4a8";
                   rowElement.innerHTML = `
                       <div class="col-4 justify-content-center">
                           <a href="#" class="d-inline-block text-truncate w-100"
                               data-bs-toggle="tooltip" data-bs-placement="bottom"
                               title="${report.book_title}" data-book-id="${report.book_id}">
                               ${report.book_title}
                           </a>
                       </div>
                       <div class="col-3 justify-content-center">
                           <a href="#" class="d-inline-block text-truncate chapter-link"
                               data-bs-toggle="tooltip" data-bs-placement="bottom"
                               title="Chapter ${report.chapter_number}" 
                               data-chapter-id="${report.chapter_id}" 
                               data-book-title="${report.book_title}" 
                               data-book-id="${report.book_id}">
                               ${report.chapter_number}
                           </a>
                       </div>
                       <div class="col-5 justify-content-center">
                           <a href="#" class="d-inline-block text-truncate"
                               data-bs-toggle="tooltip" data-bs-placement="bottom"
                               title="${report.report_content}">
                               ${report.report_content}
                           </a>
                       </div>`;

                   // Gán sự kiện khi nhấp vào hàng
                   rowElement.addEventListener("click", async function (event) {
                       event.preventDefault();

                       const chapterId = report.chapter_id;
                       const bookTitle = report.book_title;
                       const bookId = report.book_id;

                       console.log("📖 Chương được chọn có ID:", chapterId);

                       try {
                           const response = await fetch(`/get-chapter/${chapterId}`);
                           const chapter = await response.json();

                           const chapterData = {
                               number: chapter.number,
                               title: chapter.title,
                               content: chapter.content,
                               id: chapter.id,
                           };
						   console.log("chapterData:",chapterData)
                           localStorage.setItem("story-name", bookTitle);
                           localStorage.setItem("story-id", bookId);
                           localStorage.setItem("selectedChapter", JSON.stringify(chapterData));

                           console.log("✅ Lưu chương:", chapterData);
                           console.log("📕 Truyện:", localStorage.getItem("story-name"));

                           // Điều hướng đến trang chapter
                           window.location.href = "/chapter";
                       } catch (error) {
                           console.error("❌ Lỗi khi tải chương:", error);
                       }
                   });

                   // Thêm hàng vào container
                   container.appendChild(rowElement);
               });

               // Kích hoạt tooltip của Bootstrap
               var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
               tooltipTriggerList.map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));

           } catch (error) {
               console.error("❌ Lỗi khi lấy dữ liệu:", error);
           }
       });
   });

   
});
