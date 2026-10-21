import os
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

os.makedirs("figures", exist_ok=True)

N = [500, 1000, 2000, 4000, 8000, 16000]
T_seq = [1.83, 7.33, 30.65, 121.76, 485.20, 1932.67]

par = {
    1:  [2.00, 7.25, 30.63, 122.17, 484.36, 1932.78],
    2:  [1.01, 3.75, 15.70,  61.24, 243.62,  968.13],
    4:  [0.61, 2.08,  8.06,  31.53, 124.43,  498.69],
    6:  [0.59, 1.53,  5.55,  21.10,  84.15,  333.72],
    12: [0.54, 1.63,  6.26,  21.93,  86.96,  340.34],
}

S = {t: [T_seq[i] / par[t][i] for i in range(len(N))] for t in par}

tasks = [6, 30, 60, 120, 180, 300]
S_tasks = {
    500:   [3.39, 4.36, 4.26, 2.73, 2.86, 1.99],
    1000:  [5.39, 5.16, 5.51, 5.43, 4.89, 3.88],
    4000:  [5.44, 5.43, 5.59, 5.73, 5.72, 5.72],
    16000: [5.82, 5.84, 5.81, 5.82, 5.84, 5.79],
}

# T(N)
fig, ax = plt.subplots(figsize=(7, 4))
ax.plot(N, T_seq, "-o")
ax.set_xlabel("N")
ax.set_ylabel("Час кроку, мс")
# ax.set_xticks(N)
ax.grid(True)
fig.tight_layout()
fig.savefig("figures/fig-seq-time.svg", format="svg")
plt.close()

# S(N)
fig, ax = plt.subplots(figsize=(7, 4))
for i, t in enumerate([1, 2, 4, 6, 12]):
    ax.plot(N, S[t], "-o", label=f"{t} потік{'и' if t in (2,4) else 'ів' if t != 1 else ''}")
ax.set_xlabel("N")
ax.set_ylabel("Прискорення S")
ax.set_xticks(N)
ax.tick_params(axis="x", rotation=45)
ax.set_ylim(0.7, 6)
ax.legend()
ax.grid(True)
fig.tight_layout()
fig.savefig("figures/fig-speedup.svg", format="svg")
plt.close()

# S(tasks)
fig, ax = plt.subplots(figsize=(7, 4.5))
for i, n in enumerate([500, 1000, 4000, 16000]):
    ax.plot(tasks, S_tasks[n], "-o", label=f"N = {n}")
ax.set_xlabel("Кількість підзадач")
ax.set_ylabel("Прискорення S")
ax.set_xticks(tasks)
ax.tick_params(axis="x", rotation=45)
ax.set_ylim(0.7, 6)
ax.legend()
ax.grid(True)
fig.tight_layout()
fig.savefig("figures/fig-tasks.svg", format="svg")
plt.close()
