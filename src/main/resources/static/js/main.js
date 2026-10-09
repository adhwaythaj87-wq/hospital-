/**
 * LifeCare Hospital Management System - Main JavaScript
 */

document.addEventListener('DOMContentLoaded', () => {
    // 0. Mobile Navigation Toggle
    const mobileToggle = document.getElementById('mobileMenuToggle');
    const mainNav = document.getElementById('mainNavigation');
    if (mobileToggle && mainNav) {
        mobileToggle.addEventListener('click', (e) => {
            e.stopPropagation();
            const isExpanded = mobileToggle.getAttribute('aria-expanded') === 'true';
            mobileToggle.setAttribute('aria-expanded', !isExpanded);
            mobileToggle.classList.toggle('active');
            mainNav.classList.toggle('nav-open');
        });

        // Close mobile nav when clicking outside
        document.addEventListener('click', (e) => {
            if (mainNav.classList.contains('nav-open') && !mainNav.contains(e.target) && !mobileToggle.contains(e.target)) {
                mainNav.classList.remove('nav-open');
                mobileToggle.classList.remove('active');
                mobileToggle.setAttribute('aria-expanded', 'false');
            }
        });
    }

    // 1. Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        const closeBtn = alert.querySelector('.alert-close-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                alert.style.opacity = '0';
                setTimeout(() => alert.remove(), 200);
            });
        }
        setTimeout(() => {
            if (alert && alert.parentElement) {
                alert.style.transition = 'opacity 0.4s ease';
                alert.style.opacity = '0';
                setTimeout(() => alert.remove(), 400);
            }
        }, 6000);
    });

    // 2. Patient 360 Record Tabs Switching
    const tabBtns = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');
    if (tabBtns.length > 0) {
        tabBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                const targetTab = btn.getAttribute('data-tab');

                tabBtns.forEach(b => b.classList.remove('active'));
                tabContents.forEach(c => c.classList.remove('active'));

                btn.classList.add('active');
                const targetEl = document.getElementById(targetTab);
                if (targetEl) {
                    targetEl.classList.add('active');
                }
            });
        });

        // Activate tab from URL hash if present (e.g. #tab-prescriptions)
        if (window.location.hash) {
            const hash = window.location.hash.substring(1);
            const matchingBtn = document.querySelector(`.tab-btn[data-tab="${hash}"]`);
            if (matchingBtn) {
                matchingBtn.click();
            }
        }
    }

    // 3. Dynamic Prescription Medicine Rows
    const addMedBtn = document.getElementById('add-medicine-row-btn');
    const medContainer = document.getElementById('medicine-rows-container');

    if (addMedBtn && medContainer) {
        addMedBtn.addEventListener('click', () => {
            const rowCount = medContainer.querySelectorAll('.med-row').length;
            const newRow = document.createElement('div');
            newRow.className = 'med-row';
            newRow.innerHTML = `
                <div>
                    <label class="form-label">Medicine Name *</label>
                    <input type="text" name="items[${rowCount}].medicineName" class="form-control" placeholder="e.g. Paracetamol 650mg" required />
                </div>
                <div>
                    <label class="form-label">Dosage *</label>
                    <input type="text" name="items[${rowCount}].dosage" class="form-control" placeholder="e.g. 1 Tablet" required />
                </div>
                <div>
                    <label class="form-label">Frequency</label>
                    <input type="text" name="items[${rowCount}].frequency" class="form-control" value="1-0-1 (Twice Daily)" />
                </div>
                <div>
                    <label class="form-label">Duration *</label>
                    <input type="text" name="items[${rowCount}].duration" class="form-control" placeholder="e.g. 5 Days" required />
                </div>
                <div>
                    <label class="form-label">Instructions</label>
                    <input type="text" name="items[${rowCount}].instructions" class="form-control" value="After meals" />
                </div>
                <div style="align-self: flex-end; padding-bottom: 0.15rem;">
                    <button type="button" class="btn btn-danger btn-sm remove-med-row-btn" title="Remove Medicine">&times;</button>
                </div>
            `;
            medContainer.appendChild(newRow);
            bindRemoveMedButtons();
        });

        bindRemoveMedButtons();
    }

    function bindRemoveMedButtons() {
        const removeBtns = document.querySelectorAll('.remove-med-row-btn');
        removeBtns.forEach(btn => {
            btn.onclick = function() {
                const rows = medContainer.querySelectorAll('.med-row');
                if (rows.length > 1) {
                    btn.closest('.med-row').remove();
                    reindexMedicineRows();
                } else {
                    alert('At least one medicine row must be included.');
                }
            };
        });
    }

    function reindexMedicineRows() {
        const rows = medContainer.querySelectorAll('.med-row');
        rows.forEach((row, index) => {
            row.querySelectorAll('input').forEach(input => {
                const nameAttr = input.getAttribute('name');
                if (nameAttr) {
                    input.setAttribute('name', nameAttr.replace(/items\[\d+\]/, `items[${index}]`));
                }
            });
        });
    }

    // 4. Live Billing Total Calculator
    const consultInput = document.getElementById('consultationFee');
    const medInput = document.getElementById('medicineCharge');
    const labInput = document.getElementById('labCharges');
    const discountInput = document.getElementById('discount');
    const taxInput = document.getElementById('taxAmount');
    const totalDisplay = document.getElementById('totalAmountDisplay');
    const paymentStatusSelect = document.getElementById('paymentStatus');
    const paidAmountInput = document.getElementById('paidAmount');

    function calculateBillTotal() {
        if (!totalDisplay) return 0;

        const consult = parseFloat(consultInput?.value) || 0;
        const med = parseFloat(medInput?.value) || 0;
        const lab = parseFloat(labInput?.value) || 0;
        const discount = parseFloat(discountInput?.value) || 0;
        const tax = parseFloat(taxInput?.value) || 0;

        const subtotal = consult + med + lab;
        const afterDiscount = Math.max(0, subtotal - discount);
        const finalTotal = afterDiscount + tax;

        totalDisplay.innerText = '₹' + finalTotal.toFixed(2);
        return finalTotal;
    }

    [consultInput, medInput, labInput, discountInput, taxInput].forEach(inp => {
        if (inp) {
            inp.addEventListener('input', () => {
                const total = calculateBillTotal();
                if (paymentStatusSelect && paymentStatusSelect.value === 'PAID' && paidAmountInput) {
                    paidAmountInput.value = total.toFixed(2);
                }
            });
        }
    });
    calculateBillTotal();

    // 5. Payment Status Change auto-fills paid amount
    if (paymentStatusSelect && paidAmountInput) {
        paymentStatusSelect.addEventListener('change', () => {
            if (paymentStatusSelect.value === 'PAID') {
                const total = calculateBillTotal();
                paidAmountInput.value = total.toFixed(2);
            } else if (paymentStatusSelect.value === 'PENDING') {
                paidAmountInput.value = '0.00';
            }
        });
    }

    // 6. Doctor selection auto-updates consultation fee in billing form
    const doctorSelect = document.getElementById('billingDoctorSelect');
    if (doctorSelect && consultInput) {
        doctorSelect.addEventListener('change', () => {
            const selectedOption = doctorSelect.options[doctorSelect.selectedIndex];
            const fee = selectedOption.getAttribute('data-fee');
            if (fee) {
                consultInput.value = fee;
                const total = calculateBillTotal();
                if (paymentStatusSelect && paymentStatusSelect.value === 'PAID' && paidAmountInput) {
                    paidAmountInput.value = total.toFixed(2);
                }
            }
        });
    }
});
