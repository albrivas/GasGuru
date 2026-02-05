---
name: skill-gasguru-vm-testing
description: Create and update GasGuru ViewModel unit tests using fakes, JUnit5, Turbine, and coroutine test rules.
metadata:
  short-description: GasGuru VM tests with fakes
---

# GasGuru VM Testing

Use this skill when writing or updating ViewModel unit tests in GasGuru.

## Workflow
1. Identify the VM dependencies (use cases, repositories, dispatchers).
2. Prefer **real use cases** with **fake repositories/DAOs** from `core/testing`.
3. If a fake does not exist, add it under `core/testing/src/main/java/com/gasguru/core/testing/fakes/data/<layer>/`.
4. Place tests in `src/test/kotlin/...` (JVM tests) with **JUnit5** and **Turbine**.
5. Use `CoroutinesTestExtension`; inject the test dispatcher into VM where needed (do not rely on `Dispatchers.IO/Default`).
6. When asserting `StateFlow`, tolerate initial values and ensure a real state change before awaiting new emissions.

## Reference
- See `references/TESTING.md` for conventions, examples, and checklist.
