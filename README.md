# Save Token Kit - Bộ Cấu Hình Tối Ưu Hóa Token & Quota Cho AI Agent

Save Token Kit là bộ giải pháp đóng gói sẵn bao gồm **Rules (Quy tắc)** và **Skills (Kỹ năng nâng cao)** dành cho AI Agent (như Antigravity, Claude Code, v.v.). Bộ công cụ này giúp các mô hình ngôn ngữ lớn (Gemini, Claude, GPT, Codex...) hoạt động cực kỳ hiệu quả, tiết kiệm tối đa quota API và dung lượng token sử dụng bằng cách tối ưu hóa cách thức đọc/ghi tệp tin và duy trì ngữ cảnh.

---

## 📦 Thành Phần Bộ Công Cụ

Thư mục `.agents/` chứa cấu hình có thể cắm-và-chạy (plug-and-play):

1. **`rules/rule-save-token.md` (Quy tắc tối ưu luôn bật)**:
   - Định nghĩa quy trình bắt buộc Agent phải tuân thủ để tránh đọc file dung lượng lớn và giảm thiểu việc phản hồi dư thừa.
2. **`skills/save-token/SKILL.md` (Kỹ năng tối ưu hóa context)**:
   - Hướng dẫn Agent các mẹo cụ thể về quản lý file, thiết lập file ignore và thực hiện chỉnh sửa cục bộ.
3. **`skills/codegraph/SKILL.md` (Kỹ năng phân tích callgraph ngoại tuyến)**:
   - Giúp Agent hiểu kiến trúc mã nguồn thông qua call graph và dependency graph mà không cần mở đọc toàn bộ codebase.
4. **`skills/codebase-memory-mcp/SKILL.md` (Kỹ năng ghi nhớ ngữ cảnh)**:
   - Hướng dẫn lưu trữ và truy vấn tri thức thông qua MCP memory server để tránh việc phân tích lại từ đầu.
5. **`skills/autonomous-developer/SKILL.md` (Kỹ năng lập trình viên tự trị)**:
   - Kích hoạt quy trình khép kín giúp Agent tự động đọc tài liệu, viết code, chạy test/build, tự sửa lỗi (self-debug) và đồng bộ tài liệu khi nhận lệnh `/agent-skill <yêu cầu>`.
6. **`skills/git-workflow-automator/SKILL.md` (Kỹ năng tự động hóa Git)**:
   - Tự động hóa việc tạo branch, commit chuẩn Conventional Commits, soạn thảo PR description và giải quyết conflicts.
7. **`skills/api-docs-generator/SKILL.md` (Kỹ năng tự động tạo tài liệu API)**:
   - Quét controllers/routes để tự động cập nhật hoặc tạo mới file Swagger/OpenAPI spec (YAML/JSON).
8. **`skills/code-refactoring-optimizer/SKILL.md` (Kỹ năng tái cấu trúc mã nguồn)**:
   - Phát hiện code smells, nợ kỹ thuật và tự động áp dụng các Design Patterns/SOLID principles một cách an toàn.
9. **`skills/docker-composer-helper/SKILL.md` (Kỹ năng Docker)**:
   - Tối ưu hóa file Dockerfile, Docker Compose, multi-stage builds và chẩn đoán log container tự động.
10. **`skills/security-vulnerability-scanner/SKILL.md` (Kỹ năng quét lỗ hổng bảo mật)**:
   - Rà soát tĩnh mã nguồn tìm OWASP Top 10, quét lỗi phụ thuộc (dependency vulnerabilities) và vá lỗi.
11. **`skills/performance-profiler-analyser/SKILL.md` (Kỹ năng tối ưu hóa hiệu năng)**:
   - Tìm kiếm nghẽn cổ chai, rò rỉ bộ nhớ, tối ưu hóa N+1 query và triển khai các chiến lược caching (Redis).
12. **`skills/ci-cd-pipeline-orchestrator/SKILL.md` (Kỹ năng thiết lập CI/CD)**:
   - Viết và gỡ lỗi cấu hình pipeline GitHub Actions/GitLab CI cho các công đoạn lint, test, build, deploy.
13. **`skills/i18n-translator-manager/SKILL.md` (Kỹ năng quản lý đa ngôn ngữ)**:
   - Trích xuất chuỗi text cứng UI, thiết lập cấu trúc tệp locale (JSON/YAML) và quản lý dịch thuật tự động.

### 🤖 Custom Agents
14. **`agents/qa-tester/AGENT.md` (QA Tester Agent)**:
   - Chuyển đổi vai trò của AI thành một kỹ sư kiểm thử tự động chuyên nghiệp (QA Automation & Security Researcher). Tự động phân tích nghiệp vụ, viết unit/integration test, chạy test suite, kiểm tra coverage và truy lùng các edge cases/lỗi bảo mật.

---

## 🚀 Hướng Dẫn Tích Hợp Vào Dự Án Bất Kỳ

Để kích hoạt bộ tối ưu hóa này cho bất kỳ dự án mới nào của bạn, hãy chọn một trong hai cách dưới đây:

### Cách 1: Tích hợp cục bộ theo dự án (Khuyên dùng)
Nếu bạn muốn bộ quy tắc này đi kèm mã nguồn dự án của bạn (để khi chia sẻ cho người khác họ cũng được tối ưu hóa tự động):

1. Sao chép toàn bộ thư mục `.agents` từ dự án này.
2. Dán thư mục `.agents` vào thư mục gốc của dự án mới của bạn.
3. Khi bạn khởi chạy AI Agent trong dự án đó, Agent sẽ tự động quét, nhận diện và nạp các quy tắc/kỹ năng tối ưu này.

### Cách 2: Tích hợp toàn cục (Global)
Nếu bạn muốn áp dụng bộ tối ưu hóa này cho **TẤT CẢ** các dự án bạn mở trên máy tính mà không cần copy thư mục `.agents` vào từng dự án:

1. Copy các file trong `.agents/rules/` vào thư mục cấu hình global của Agent trên máy bạn:
   - **Đường dẫn mặc định**: `C:\Users\<Tên_User>\.gemini\config\rules\` (Hoặc append trực tiếp nội dung vào file `AGENTS.md` trong thư mục `C:\Users\<Tên_User>\.gemini\config\`).
2. Copy các thư mục skill trong `.agents/skills/` vào thư mục skill global:
   - **Đường dẫn mặc định**: `C:\Users\<Tên_User>\.gemini\config\skills\`

---

## 🛠️ Hướng Dẫn Sử Dụng Chi Tiết

Khi Agent đã nhận diện bộ cấu hình, bạn có thể yêu cầu:
- `"Hãy sử dụng save-token skill để tối ưu hóa việc phân tích tệp này."`
- `"Hãy dựng codegraph cho module Auth để hiểu luồng gọi hàm."`
- Quy tắc `rule-save-token.md` sẽ tự động kích hoạt chế độ tiết kiệm token và quota cho Agent trong mỗi lượt trao đổi mà bạn không cần phải nhắc nhở thêm.
