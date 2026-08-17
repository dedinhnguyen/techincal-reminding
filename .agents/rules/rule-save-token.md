---
trigger: always_on
---

# Quy Tắc Tối Ưu Hóa Token và Tiết Kiệm Quota (Token & Quota Optimization Rules)

Để đảm bảo hiệu suất hoạt động, giảm thiểu chi phí API quota, và tối ưu hóa context window cho bất kỳ mô hình ngôn ngữ nào (Gemini, Claude, Codex, GPT...), Agent **BẮT BUỘC** phải tuân thủ nghiêm ngặt các quy tắc dưới đây trong suốt quá trình làm việc.

---

## 🔍 1. Chiến Lược Tìm Kiếm & Đọc File Thông Minh (Read Operations)

1. **Kiểm tra trước khi đọc**:
   - Không được dùng `view_file` đọc toàn bộ nội dung của tệp tin có kích thước lớn (> 200 dòng) hoặc chưa rõ dung lượng.
   - Luôn sử dụng `grep_search` để định vị từ khóa, định nghĩa hàm/lớp trước, sau đó chỉ đọc phân đoạn cụ thể bằng cách chỉ định `StartLine` và `EndLine` trong `view_file`.
   
2. **Hạn chế liệt kê đệ quy**:
   - Tránh chạy lệnh `list_dir` đệ quy sâu trên các thư mục lớn chứa nhiều tệp tin tạm thời (như `node_modules`, `build`, `dist`, `.git`).
   - Luôn lọc file bằng cấu hình loại trừ hoặc chỉ định mục tiêu rõ ràng.

---

## ✍️ 2. Chỉnh Sửa File Tối Giản & Tránh Chép Lại Code (Write Operations)

1. **Ưu tiên chỉnh sửa cục bộ**:
   - **CẤM** sử dụng `write_to_file` để ghi đè toàn bộ nội dung của một tệp tin hiện có nếu chỉ thay đổi một vài dòng.
   - Luôn sử dụng `replace_file_content` (cho thay đổi đơn, liên tục) hoặc `multi_replace_file_content` (cho nhiều thay đổi không liên tục) để chỉ cập nhật đúng các dòng cần sửa. Việc này tiết kiệm đáng kể token đầu ra (output tokens).

2. **Không sao chép lại mã nguồn trong Chat**:
   - Khi trả lời người dùng, **không sao chép lại các đoạn code lớn** đã thay đổi hoặc đang thảo luận. 
   - Thay vào đó, hãy sử dụng Markdown links trỏ trực tiếp đến file và số dòng tương ứng (ví dụ: `[main.js](file:///path/to/main.js#L12-L24)`).

---

## 💬 3. Giao Tiếp Ngắn Gọn & Tập Trung (Communication)

1. **Phản hồi súc tích**:
   - Tránh việc giải thích dài dòng các bước triển khai kỹ thuật hiển nhiên. Chỉ giải thích các quyết định thiết kế quan trọng, kiến trúc hệ thống hoặc các cảnh báo bảo mật.
   - Giữ câu trả lời trực quan, tập trung vào kết quả và hành động tiếp theo.

2. **Sử dụng Artifacts thông minh**:
   - Đối với các báo cáo, tài liệu thiết kế hoặc kế hoạch lớn, hãy viết trực tiếp vào file tài liệu trong thư mục `.agents` hoặc `docs/`, sau đó chỉ gửi link cho người dùng. Không in toàn bộ nội dung tài liệu ra cửa sổ chat.

---

## 🔄 4. Quản Lý Tiến Trình Và Trạng Thái (Task Management)

1. **Tránh Polling liên tục**:
   - Không chạy vòng lặp kiểm tra trạng thái (`manage_task status`) của các tác vụ chạy nền.
   - Hãy sử dụng `schedule` để hẹn giờ thức dậy sau một khoảng thời gian hợp lý hoặc để hệ thống tự động đánh thức khi tác vụ hoàn thành.

2. **Duy trì bộ nhớ cục bộ**:
   - Sử dụng tệp `task.md` để ghi nhận các bước công việc đang làm dở, tránh việc tự phân tích lại từ đầu sau mỗi lượt chat (giảm token phân tích).
