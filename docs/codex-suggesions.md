# Codex Suggestions

## User Group Model
- Security rules revolve around three Keycloak realm roles: `nexus-user`, `nexus-manager`, and `nexus-admin`.
- Route protection follows a strict hierarchy: admin > manager > user.
- GraphQL access relies on the same role set, so new resolvers should reuse those annotations.

## User Provisioning Flow
- `KeycloakUserService` seeds the three baseline accounts in dev and is reused for bespoke creations.
- Admin API `POST /api/admin/users/create` accepts a password and roles list, delegating to the same service.
- Created passwords are marked non-temporary (`temporary=false`), so first-login reset is not required.

## Opportunities
- There is no REST endpoint for users to change their own password; consider exposing a secure flow or documenting the Keycloak UI workaround.
- Detecting duplicate emails depends on Keycloak feedback; adding explicit pre-checks could improve UX.
- Audit logging around admin-driven user creation would help track credential lifecycles.

## API Route Conventions
- Prefix endpoints with `/api/v1/<domain>/...` so versioning and bounded contexts stay visible (e.g., `/api/v1/hr/employees`, `/api/v1/payroll/runs`).
- Use plural resource nouns and HTTP verbs for CRUD, reserving verb-like suffixes for transitions (`POST /api/v1/payroll/runs/{id}/finalize`).
- Keep nesting shallow: sub-resources clarify ownership (`/api/v1/hr/employees/{id}/documents`) while tenant scoping stays in claims unless the URL must expose it.

## Anticipated Modules & Base Routes
- Identity & Access: `/api/v1/auth/*` (login, token refresh, role delegation)
- Tenant Administration: `/api/v1/admin/*` (client provisioning, feature toggles)
- Human Resources: `/api/v1/hr/*` (employees, org units, documents)
- Payroll: `/api/v1/payroll/*` (pay runs, schedules, bank exports)
- Time & Attendance: `/api/v1/time/*` (timesheets, leave requests, approvals)
- Benefits: `/api/v1/benefits/*` (plans, enrollment, carrier feeds)
- Expenses: `/api/v1/expenses/*` (claims, policy rules, reimbursements)
- Performance & Talent: `/api/v1/performance/*` (reviews, goals, feedback)
- Reporting & Analytics: `/api/v1/reports/*` (dashboards, data extracts)
- Support & Audit: `/api/v1/support/*` (impersonation, audit trails, tickets)

## GraphQL Organization
- Continue exposing a single `/graphql` endpoint; keep schemas modular via `src/main/resources/schema/<domain>.graphqls` per bounded context.
- Group root fields by domain (e.g., `Query` includes `hr`, `payroll`, `benefits` objects that delegate to `HRQuery`, `PayrollQuery`, etc.).
- Use clear field naming conventions: plurals for collections (`payrollRuns`), verb phrases for actions (`finalizePayrollRun`), and version with `@deprecated` before breaking changes.
- Mirror REST auth: annotate data fetchers with the same role checks (`@PreAuthorize("hasRole('payroll-manager')")`).
- Keep input/output types domain-prefixed (`PayrollRunInput`, `HRLeaveRequestPayload`) so schema remains navigable as modules grow.

## Soft Delete Strategy
- Add `deletedAt` (timestamp) or `isDeleted` flag to `User` so records stay in `users` table.
- Update `UserService.deleteUser` to set the flag instead of calling `deleteById`, preserving `EntityNotFoundException` for missing ids.
- Adjust repository queries (e.g., `findAllOrderByCreatedAtDesc`, `findByUsername`) to filter out soft-deleted rows, or annotate the entity with `@Where`.
- Expose restore tooling later by toggling the same flag, keeping audit history intact.

## Pricing Versioning
- Keep a `price_schedule` table with `effective_start`, `effective_end`, and `rate` so historic lookups match the period being invoiced.
- Snapshot the negotiated price onto each invoice line (`unitPrice`, `rateId`) when the invoice is generated; never recompute from the current catalog.
- Support backdated invoices by querying the schedule for the target service date (e.g., `WHERE :serviceDate BETWEEN effective_start AND COALESCE(effective_end, 'infinity')`).
- For auditability, expose GraphQL queries to fetch both the invoice value and the underlying schedule version used at the time.
