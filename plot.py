import matplotlib.pyplot as plt
from matplotlib.widgets import Button

if __name__ == "__main__":
    try:
        with open("t.txt", "r") as file:
            t = list(map(float, file.readlines()))
        with open("phi1.txt", "r") as file:
            phi1 = list(map(float, file.readlines()))
        with open("phi2.txt", "r") as file:
            phi2 = list(map(float, file.readlines()))
        with open("phi4.txt", "r") as file:
            phi4 = list(map(float, file.readlines()))
        with open("phi5.txt", "r") as file:
            phi5 = list(map(float, file.readlines()))

        with open("results_new.txt", "r") as file:
            lines = file.readlines()
            t_pa9, phi1_pa9, phi2_pa9, phi4_pa9, phi5_pa9 = [], [], [], [], []
            for i in range(1, len(lines) - 1):
                values = list(map(float, lines[i].split()))
                t_pa9.append(values[0])
                phi1_pa9.append(values[1])
                phi2_pa9.append(values[2])
                phi4_pa9.append(values[3])
                phi5_pa9.append(values[4])
    except FileNotFoundError:
        print("Файл не найден")
        exit(1)

    # Набор графиков для листания
    plots = [
        ("phi1", phi1, phi1_pa9),
        ("phi2", phi2, phi2_pa9),
        ("phi4", phi4, phi4_pa9),
        ("phi5", phi5, phi5_pa9),
    ]

    idx = 0

    fig, ax = plt.subplots(figsize=(8, 8))
    plt.subplots_adjust(bottom=0.2)

    def draw():
        ax.clear()
        name, y1, y2 = plots[idx]
        ax.plot(t, y1, label=name)
        ax.plot(t_pa9, y2, label=f"{name}_pa9")
        ax.set_xlabel("t")
        ax.set_title(f"График {name}")
        ax.legend()
        ax.grid(True)
        fig.canvas.draw_idle()

    def next_plot(event):
        global idx
        idx = (idx + 1) % len(plots)
        draw()

    def prev_plot(event):
        global idx
        idx = (idx - 1) % len(plots)
        draw()

    # Кнопки
    ax_prev = plt.axes([0.25, 0.05, 0.15, 0.075])
    ax_next = plt.axes([0.6, 0.05, 0.15, 0.075])

    btn_prev = Button(ax_prev, "◀ Назад")
    btn_next = Button(ax_next, "Вперёд ▶")

    btn_prev.on_clicked(prev_plot)
    btn_next.on_clicked(next_plot)

    draw()
    plt.show()
