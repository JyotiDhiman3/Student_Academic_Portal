# Student Academic Portal

Tech stack used-

-> Postgres

-> Java

-> HTML, CSS, Javascript

Designed and implemented a multi-user database application for managing the academics of our academic institute. The functionalities supported are based on the academic policies of our institute.The system basically consists of the following stakeholders:
● Students
● Faculty
● Academics Office

Our database comprises of the following concepts:

1. **Course Catalog:** This contains all the list of courses which can be offered in IIT Ropar. For each
course, we have information on its credit structure (L-T-P) and list of prerequisites (if any).
2. **Course Offerings:** Each semester, a faculty offers one or multiple courses. These courses should
be present in the course catalog. With each course offering, the instructors may define constraints
on CGPA (e.g., CGPA > 7.0).
3. **Student Course Registration:** A student registers for one or more courses. However, the number of
credits he/she is allowed is governed by the scheme governed by the institute (1.25 times the
average of the credits earned in the previous two semesters.
4. **Report Generation:** Staff in the academic office need to generate various kinds of reports (e.g.,
transcripts of students)
5. **Grade entry by Course Instructors:** Instructors upload the grades of students in a course via a file.
6. **User Authentication:** All the users must be authenticated before login.

   
