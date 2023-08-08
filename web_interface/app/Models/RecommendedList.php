<?php

namespace App\Models;
use Illuminate\Database\Eloquent\Model;

class RecommendedList extends Model
{
    public $timestamps = false;
    protected $table = 'recommended_loans'; 
    protected $fillable = ['status'];
}
