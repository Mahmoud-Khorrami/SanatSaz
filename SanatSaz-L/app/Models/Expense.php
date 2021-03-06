<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Expense extends Model
{
    use HasFactory;

    protected $fillable = [
        'user_id',
        'company_id',
        'factor_number',
        'date',
        'sum',
        'payment',
        'remain',
        'account_id',
        'description',
    ];

    public function expenseDetails()
    {
        return $this->hasMany(ExpenseDetail::class);
    }

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function company()
    {
        return $this->belongsTo(Company::class);
    }

    public function account()
    {
        return $this->belongsTo(Account::class);
    }

}
