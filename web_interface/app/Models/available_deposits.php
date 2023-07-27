<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class available_deposits extends Model
{
    protected $fillable = ['member_number', 'amount_deposited', 'deposit_date', 'receipt_number'];

    use HasFactory;
}
